package com.markerhub.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;


public class LinuxFile {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String host;

    private String user;

    private String password;

    private int port;

    private Session session;

    private static ChannelSftp channel = null;

    public LinuxFile(String host, String user, String password, int port) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    private void initialSession() throws Exception {
        if (session == null) {
            JSch jsch = new JSch();
            logger.info("调用jsch.getSession方法验证安全用户获取session");
            session = jsch.getSession(user, host, port);
            logger.info("session:"+session);
            logger.info("调用session的setUserInfo方法");
            session.setUserInfo(new UserInfo() {

                public String getPassphrase() {
                    return null;
                }

                public String getPassword() {
                    return null;
                }

                public boolean promptPassword(String arg0) {
                    return false;
                }

                public boolean promptPassphrase(String arg0) {
                    return false;
                }

                public boolean promptYesNo(String arg0) {
                    return true;
                }

                public void showMessage(String arg0) {
                }

            });
            logger.info("调用session的setPassword方法");
            session.setPassword(password);
            logger.info("调用session的setPassword方法调用完毕："+password);
            logger.info("调用session的connect()方法");
            session.connect();
            logger.info("调用session的connect()方法完毕~~~~~~~~~~~~~~~~~~~~~");
        }
    }
    /**
     * 关闭连接
     * @throws Exception
     */
    public void close() throws Exception {
        if (session != null && session.isConnected()) {
            session.disconnect();
            session = null;
        }
    }
    /**
     * 上传文件
     *
     * @param localPath
     *            本地路径，若为空，表示当前路径
     * @param localFile
     *            本地文件名，若为空或是“*”，表示目前下全部文件
     * @param remotePath
     *            远程路径，若为空，表示当前路径，若服务器上无此目录，则会自动创建
     * @throws Exception
     */
    public String uploadFile(String localPath, String localFile, String remotePath)
            throws Exception {
        this.initialSession();
        Channel channelSftp = session.openChannel("sftp");
        channelSftp.connect();
        ChannelSftp c = (ChannelSftp) channelSftp;
        String remoteFile = null;
        if (remotePath != null && remotePath.trim().length() > 0) {
            try {
                c.mkdir(remotePath);
            } catch (Exception e) {
            }
            remoteFile = remotePath + "/";
        } else {
            remoteFile = ".";
        }
        String file = null;
        if (localFile == null || localFile.trim().length() == 0) {
            file = "*";
        } else {
            file = localFile;
        }
        if (localPath != null && localPath.trim().length() > 0) {
            if (localPath.endsWith("/")) {
                file = localPath + file;
            } else {
                file = localPath  + file;
            }
        }
        c.put(file, remoteFile);
        channelSftp.disconnect();
        return "上传成功";
    }
    /**
     * 下载文件
     *
     * @param remoteFile
     *            服务器文件名，若为空，表示当前路径
     * @param remotePath
     *            服务器路径，若为空或是“*”，表示目前下全部文件
     * @param localFile
     *            本地路径，若为空，表示当前路径，若服务器上无此目录，则会自动创建
     * @throws Exception
     */
    public String downloadFile(String remoteFile, String remotePath, String localFile) throws Exception {
        logger.info("通过initialSession验证安全用户获取session");
        this.initialSession();
        logger.info("验证完毕");
        logger.info("启动channelSftp");
        Channel channelSftp = session.openChannel("sftp");
        logger.info("调用channelSftp的connect方法");
        channelSftp.connect();
        logger.info("调用完成");
        logger.info("打开终端");
        ChannelSftp c = (ChannelSftp) channelSftp;
        logger.info("打开linux终端："+c);
        OutputStream output = null;
        File file = null;
        try {
            file = new File(localFile+remoteFile);
            if (!checkFileExist(localFile)) {
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            c.cd(remotePath);
            logger.info("cd进入目录:"+remotePath);
            c.get(remoteFile, output);
            logger.info("调用SFTP下载："+remoteFile+"文件，file："+output);
            return "下载成功";
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("下载出错");
        } finally {
            if (output != null) {
                try {
                    output.close();
                    logger.info("关闭file流");
                } catch (IOException e) {
                    throw new Exception("关闭流错误");
                }
            }

        }
    }
    /**
     * 查询文件
     *
     * @param remoteFile
     *            服务器文件名，若为空，表示当前路径
     * @param remotePath
     *            服务器路径，若为空或是“*”，表示目前下全部文件
     * @throws Exception
     */
    public String selectwjxj(String remoteFile, String remotePath) throws Exception {
        this.initialSession();
        Channel channelSftp = session.openChannel("sftp");
        channelSftp.connect();
        ChannelSftp c = (ChannelSftp) channelSftp;
        OutputStream output = null;
        try {
            c.cd(remotePath);
            c.ls(remotePath);
            try {
                String wj = c.ls(remoteFile).toString();
                return "文件存在";
            }catch (Exception e){
                return "文件不存在";
            }
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("下载出错");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new Exception("关闭流错误");
                }
            }

        }
    }

    private boolean checkFileExist(String localPath) {
        File file = new File(localPath);
        return file.exists();
    }

    public static void main(String[] args) {
       LinuxFile linuxFile = new LinuxFile("192.168.252.128", "root", "root", 22);
        try {
            //测试查询
            // String selectwjxj = linuxFile.selectwjxj("2020年06月29日工作日报.docx", "/alwj/981a9874123/");
            //System.out.println(selectwjxj);
            //测试下载
          //  linuxFile.downloadFile("2020年06月29日工作日报.docx","/alwj/981a9874123/","E:\\gp\\xm\\xawj\\981a9874123");
        linuxFile.downloadFile("2.jpg","/alwj/4ingogws59u00000","E:\\gp\\xm\\xawj\\18b11310-7477-4370-aea1-9436dd79c2a7\\");
            //测试上传
//            linuxFile.uploadFile("E:\\gp\\xm\\alwj\\981a9874123\\", "2020年06月29日工作日报.docx", "/alwj/981a9874123");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                linuxFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // command 命令
    public String runCommand(String command) throws Exception {
        // CommonUtil.printLogging("[" + command + "] begin", host, user);

        this.initialSession();
        InputStream in = null;
        InputStream err = null;
        BufferedReader inReader = null;
        BufferedReader errReader = null;
        int time = 0;
        String s = null;
        boolean run = false;
        StringBuffer sb = new StringBuffer();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(null);
        err = ((ChannelExec) channel).getErrStream();
        in = channel.getInputStream();
        channel.connect();
        inReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        errReader = new BufferedReader(new InputStreamReader(err, "UTF-8"));

        while (true) {
            s = errReader.readLine();
            if (s != null) {
                sb.append("error:" + s).append("\n");
            } else {
                run = true;
                break;
            }
        }
        while (true) {
            s = inReader.readLine();
            if (s != null) {
                sb.append("info:" + s).append("\n");
            } else {
                run = true;
                break;
            }
        }

        while (true) {
            if (channel.isClosed() || run) {
                // CommonUtil.printLogging("[" + command + "] finish: " +
                // channel.getExitStatus(), host, user);
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
            if (time > 180) {
                // CommonUtil.printLogging("[" + command + "] finish2: " +
                // channel.getExitStatus(), host, user);
                break;
            }
            time++;
        }

        inReader.close();
        errReader.close();
        channel.disconnect();
        session.disconnect();
        System.out.println(sb.toString());
        return sb.toString();
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param sPath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean DeleteFolder(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }
    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

}