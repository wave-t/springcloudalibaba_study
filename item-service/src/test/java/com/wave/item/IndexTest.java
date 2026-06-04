package com.wave.item;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

// es 的 RestHighLevelClient 测试类
public class IndexTest {

    private RestHighLevelClient client;

    //BeforeEach 初始化RestHighLevelClient
    @BeforeEach
    public void init() {
        //创建RestHighLevelClient
        client = new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create ("http://192.168.80.129:9200")));
    }

    //测试创建索引库方法
    @Test
    void testCreateIndex() throws IOException {
        //创建Request对象,指定索引名items
        CreateIndexRequest request = new CreateIndexRequest("items");
        //添加请求参数
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        //发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }
    //测试删除索引库方法
    @Test
    void testDeleteIndex() throws IOException {
        //创建Request对象,指定索引名items
        DeleteIndexRequest request = new DeleteIndexRequest("items");
        //发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }
    //测试判断索引是否存在方法
    @Test
    void testExist() throws IOException {
        //创建Request对象,指定索引名items
        GetIndexRequest request = new GetIndexRequest("items");
        //调用exist方法判断索引是否存在
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("索引items"+(exists?"存在":"不存在"));
    }

    //AfterEach 关闭RestHighLevelClient
    @AfterEach
    void close() throws Exception {
        this.client.close();
    }

    //定义常量 存放mapping json字符串
    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"stock\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"commentCount\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"isAD\":{\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"updateTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
