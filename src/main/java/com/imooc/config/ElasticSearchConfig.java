/*********************************************************
 *********************************************************
 ********************                  *******************
 *************                                ************
 *******                  _oo0oo_                  *******
 ***                     o8888888o                     ***
 *                       88" . "88                       *
 *                       (| -_- |)                       *
 *                       0\  =  /0                       *
 *                     ___/`---'\___                     *
 *                   .' \\|     |// '.                   *
 *                  / \\|||  :  |||// \                  *
 *                 / _||||| -:- |||||- \                 *
 *                |   | \\\  -  /// |   |                *
 *                | \_|  ''\---/''  |_/ |                *
 *                \  .-\__  '-'  ___/-. /                *
 *              ___'. .'  /--.--\  `. .'___              *
 *           ."" '<  `.___\_<|>_/___.' >' "".            *
 *          | | :  `- \`.;`\ _ /`;.`/ - ` : | |          *
 *          \  \ `_.   \_ __\ /__ _/   .-` /  /          *
 *      =====`-.____`.___ \_____/___.-`___.-'=====       *
 *                        `=---='                        *
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~      *
 *********__佛祖保佑__永无BUG__验收通过__钞票多多__*********
 *********************************************************/
package com.imooc.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: ElasticSearchConfig
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: 公司名称
 * @date: 2019年5月13日 上午11:25:19
 * 
 * @Copyright: 2019 www.xxx.com Inc. All rights reserved.
 *             注意：本内容仅限于公司内部传阅，禁止外泄以及用于其他的商业目
 */
@Configuration
public class ElasticSearchConfig {

	@Bean
	public TransportClient esClient() throws UnknownHostException {
		Settings settings = Settings.builder()
				.put("cluster.name", "xunwu")
				.put("client.transport.sniff", false).build();
		
		/*常见问题

		ip问题
		当ES服务器监听使用内网服务器IP而访问使用外网IP时，不要使用client.transport.sniff为true，在自动发现时会使用内网IP进行通信，导致无法连接到ES服务器，而直接使用addTransportAddress方法进行指定ES服务器。

		版本问题
		使用的elasticsearch 5.4.0版本，API使用的5.2.1，client.transport.sniff = true ，连接和查询正常

		使用的elasticsearch 5.4.0版本，API使用的5.4.0，client.transport.sniff = true ，查询时报出异常
		
				原因是client没有连接上，当把client.transport.sniff = false的时候，可以正常连接和查询。也就是说，API 5.4.0 可能会出现这个问题，具体原因还不清楚。

				处理方式：client.transport.sniff = false 就无法自动搜索到集群中的其他节点，所以，需要将节点手动添加到client中。*/
		InetSocketTransportAddress master = new InetSocketTransportAddress(
				InetAddress.getByName("39.104.100.138"), 9300);
		TransportClient transportClient = new PreBuiltTransportClient(settings)
				.addTransportAddress(master);
		return transportClient;
	}
}
