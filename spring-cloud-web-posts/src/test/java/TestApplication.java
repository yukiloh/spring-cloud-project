import com.test.spring.cloud.web.posts.service.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class TestApplication {

    @Autowired
    RedisService redisService;

    @Value("${hosts.sso}")
    private String foo;




    @Test
    public void testContext (){

        /*测试redis用，bean有问题*/
        redisService.put("111", "111", 30 * 60);/*在redis中存放token（内含loginCode）,结果为"ok"或者null*/
        redisService.put("222", "222", 30 * 60);/*在redis中存放token（内含loginCode）,结果为"ok"或者null*/
        redisService.put("333", "333", 30 * 60);/*在redis中存放token（内含loginCode）,结果为"ok"或者null*/
        redisService.put("444", "444", 30 * 60);/*在redis中存放token（内含loginCode）,结果为"ok"或者null*/
        redisService.put("555", "555", 30 * 60);/*在redis中存放token（内含loginCode）,结果为"ok"或者null*/
        redisService.put("666", "666", 30 * 60);/*在redis中存放token（内含loginCode）,结果为"ok"或者null*/


    }

}
