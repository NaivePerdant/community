package top.perdant.community.provider;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import top.perdant.community.dto.AccessTokenDTO;
import top.perdant.community.dto.GitHubUser;

import java.util.concurrent.TimeUnit;

/**
 * 第三方认证获取token和user信息
 *
 * @author perdant
 */
@Component
public class GitHubProvider {
    /**
     * httpClient 发送 post 请求，获取 accessToken
     *
     * @param accessTokenDTO 封装了需要发送的参数
     * @return accessToken
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        // 把AccessTokenDTO 类快速转换为 JSON 放入 requestBody 中
        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        // 简单的 httpClient 向某个地址发送请求
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            // response 中形如 access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
            // 我们只要access_token这一部分
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * httpClient 发送 get 请求，获取 user 信息
     *
     * @param accessToken
     * @return 封装的 GitHubUer 信息
     */
    public GitHubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10,TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                // accessToken放在url中的写法即将在21年9月份作废，修改成放在请求头中的形式
                .url("https://api.github.com/user")
                .header("Authorization","token "+accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String gitHubUserStr = response.body().string();
            // 把 JSON 快速转换成 GitHubUser 类并且把下划线标识自动映射为驼峰标识
            GitHubUser gitHubUser = JSON.parseObject(gitHubUserStr, GitHubUser.class);
            return gitHubUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
