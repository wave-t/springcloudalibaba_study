package com.wave.item;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wave.common.utils.BeanUtils;
import com.wave.common.utils.CollUtils;
import com.wave.item.domain.dto.ItemDoc;
import com.wave.item.domain.po.Item;
import com.wave.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

//es 文档操作测试类
@SpringBootTest(properties = "spring.profiles.active=local")
public class DocumentTest {

    //声明RestHighLevelClient
    private RestHighLevelClient client;
    //声明业务类Iservice
    @Autowired
    private IItemService service;

    //Beforeech 测试方法之前创建Es连接
    @BeforeEach
    void inti() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.80.129:9200")
        ));
    }

    //测试方法 测试创建文档
    @Test
    void testIndexDocument() throws IOException {
        //根据ID查询Item对象
        Item item = service.getById(100002644680L);
        //将item转为itemDoc
        ItemDoc itemDoc = BeanUtils.copyProperties(item, ItemDoc.class);
        //将itemDoc转为json
        String doc = JSONUtil.toJsonStr(itemDoc);
        //创建Request对象，指定索引库名和文档id
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());
        //准备json文档
        request.source(doc, XContentType.JSON);
        //发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    //测试方法 测试查询文档
    @Test
    void testGetDocument() throws IOException {
        //创建Request对象，指定索引库名和文档id
        GetRequest request = new GetRequest("items","100002644680");
        //发送请求 获取响应结果
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
        //获取响应结果中的source
        String sourceAsString = getResponse.getSourceAsString();
        //将结果反序列化为java对象
        ItemDoc itemDoc = JSONUtil.toBean(sourceAsString, ItemDoc.class);
        System.out.println(itemDoc);
    }

    //测试方法 测试删除文档
    @Test
    void testDeleteDocument() throws IOException {
        //创建Request对象，指定索引库名和文档id
        DeleteRequest request = new DeleteRequest("items","100002644680");
        //发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    //测试方法 测试局部更新文档
    @Test
    void testUpdateDocument() throws IOException {
        //创建Request对象，指定索引库名和文档id
        UpdateRequest request = new UpdateRequest("items","100002644680");
        //准备参数
        request.doc("price",58800, "commentCount",1);
        //发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    //测试方法 测试批量导入文档 每次获取1000条数据
    @Test
    void testBatchImport() throws IOException {
        //分页查询商品
        int page = 1;
        int size = 1000;
        while (true) {
            Page<Item> page1 = service.lambdaQuery().eq(Item::getStatus, 1).page(new Page<Item>(page, size));
            //从分页对象中获取 商品集合
            List<Item> records = page1.getRecords();
            //非空判断
            if (CollUtils.isEmpty( records)){
                //数据已经取完
                return;
            }
            //创建批量处理对象 BulkRequest 指定索引库名
            BulkRequest request = new BulkRequest("items");
            //准备参数，将集合中的所有商品对象add到批处理中
            records.forEach(item -> {
                //将item对象转为itemDoc
                ItemDoc itemDoc = BeanUtils.copyProperties(item, ItemDoc.class);
                request.add(new IndexRequest().id(itemDoc.getId()).source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON));
            });
            //执行批处理请求
            client.bulk(request, RequestOptions.DEFAULT);
            //翻页
            page++;
        }
    }

    //afterEach 测试方法之后关闭Es连接
    @AfterEach
    void destroy() throws IOException {
        this.client.close();
    }
}
