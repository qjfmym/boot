package com.example.boottest.test.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WeChatUtil {

    /**
     * 保存应用密钥
     */
    private static Map<String, AccessToken> tokenMap = new HashMap<String, AccessToken>();

    /**
     * 企业ID(企业微信*管理后台-企业信息-企业ID)
     */
    private static String corpid = "wwab291a3f7d85c8a7";

    /**
     * 获取应用access_token
     * API URL:https://work.weixin.qq.com/api/doc#90000/90135/91039
     *
     * @param corpsecret 应用的凭证密钥
     * @return
     * @throws Exception
     */
    public static String getAccessToken(String corpsecret) throws Exception {
        AccessToken token = tokenMap.get(corpsecret);
        //为空或者过期重新获取应用密钥
        if (token != null && token.getExpiresIn() > System.currentTimeMillis()) {
            return token.getAccessToken();
        }
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
        String param = "corpid=" + corpid + "&corpsecret=" + corpsecret;
        String result = HttpRequestUtil.sendGet(url, param);

        JSONObject obj = JSONObject.parseObject(result);
        int errcode = obj.getInteger("errcode");
        if (errcode == 0) {
            String accessToken = obj.getString("access_token");
            long expriseIn = obj.getLong("expires_in");
            token = new AccessToken();
            token.setAccessToken(accessToken);
            token.setExpiresIn(System.currentTimeMillis() + expriseIn * 1000);
            tokenMap.put(corpsecret, token);
            return accessToken;
        } else {
            System.out.println("获取token出错 corpsecret:" + corpsecret + " result:" + result);
            return null;
        }
    }


    /**
     * 发送消息（文本）
     * API URL:https://work.weixin.qq.com/api/doc#90000/90135/90236
     *
     * @param user    成员ID(企业微信*管理后台-通讯录-账号)
     * @param message 消息内容
     * @return
     * @throws Exception
     */
    public static boolean sendMessage(String user, String message) throws Exception {
        String corpsecret = "tV3dhXCYhEAKuaDLs4OhHaaG9GWxTKFptEz6BP5u8tE";//应用的Secret
        String token = WeChatUtil.getAccessToken(corpsecret);//获取access_token
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touser", user);//成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）
        map.put("toparty", "");//部门ID列表，多个接收者用‘|’分隔，最多支持100个。
        map.put("totag", "");//标签ID列表，多个接收者用‘|’分隔，最多支持100个
        map.put("msgtype", "text");//消息类型，此时固定为：text
        map.put("agentid", "1000002");//工作通知企业应用的id，整型(AgentId)
        Map<String, Object> content = new HashMap<String, Object>();
        content.put("content", message);
        map.put("text", content);//消息内容，最长不超过2048个字节，超过将截断
        String result = HttpRequestUtil.sendPost(url, JSON.toJSONString(map));

        boolean isSuccess = false;
        JSONObject obj = JSONObject.parseObject(result);
        int errcode = obj.getInteger("errcode");//返回errcode
        if (errcode == 0) {
            isSuccess = true;
            String[] users = user.split("\\|");
            for (String u : users) {
                System.out.println("给" + u + "发送文本消息成功:" + message + ",返回:" + result);
            }
        } else {
            String[] users = user.split("\\|");
            for (String u : users) {
                System.out.println("给" + u + "发送文本消息失败:" + message + ",返回:" + result);
            }
            isSuccess = false;
        }
        return isSuccess;
    }

    public static void main(String[] args) throws Exception {
        //测试发送企业微信文本消息
        boolean result = sendMessage("QianJiaFu", "臭啊福!");
        System.out.println(result);
    }
}
