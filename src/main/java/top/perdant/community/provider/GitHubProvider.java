package top.perdant.community.provider;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;
import top.perdant.community.dto.AccessTokenDTO;
import top.perdant.community.dto.GitHubUser;


@Component
public class GitHubProvider {
    /**
     * 使用 OkHttp 和 fastjson
     * POST 把参数放在 request body 里
     * 发送 client_id client_secret code redirect_uri state
     * 获取 response 中的 token
     * @param accessTokenDTO 封装了需要发送的参数
     * @return accessToken
     */
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        // 把AccessTokenDTO 类快速转换为 JSON
        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO), mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            // response 中形如 access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
            // 我们只要前一部分
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * GET 把参数放在 url 里
     * 发送 token 获取 user 信息
     * @param accessToken
     * @return 封装的 GitHubUer 信息
     */
    public GitHubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            // 把 JSON 快速转换成 GitHubUser 类
            GitHubUser gitHubUser = JSON.parseObject(string, GitHubUser.class);
            return gitHubUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
