//package com.mario.es.spring;
//
//import com.mario.common.util.StringUtil;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.UnknownHostException;
//import java.util.HashSet;
//import java.util.Properties;
//import java.util.Set;
//import java.util.regex.Pattern;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.TransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//
//@Slf4j
//public class TransportClientFactoryBean implements FactoryBean<TransportClient>, InitializingBean,
//    DisposableBean {
//
//  private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");
//
//  @Setter
//  private String clusterNodes;//"127.0.0.1:9300,127.0.0.1:9300";
//  @Setter
//  private TransportClient client;
//  @Setter
//  private Properties properties;
//
//  @Override
//  public void afterPropertiesSet() throws Exception {
//    if (properties == null) {
//      throw new IllegalArgumentException("ES Properties config must not null");
//    }
//
//    if (StringUtil.isBlank(clusterNodes)) {
//      throw new IllegalArgumentException(
//          "ES property[clusterNodes] must not null eg. >> 127.0.0.1:9300");
//    }
//
//    buildClient();
//  }
//
//  protected void buildClient() throws Exception {
//    if (client == null) {
//      client = new PreBuiltTransportClient(settings());
//      parseHostAndPort().forEach(inetSocketAddress -> client
//          .addTransportAddresses(new TransportAddress(inetSocketAddress)));
//    }
//  }
//
//  private Settings settings() {
//    if (properties != null) {
//      Settings.Builder builder = Settings.builder();
//      for (String propertyName : properties.stringPropertyNames()) {
//        Object orDefault = properties.getOrDefault(propertyName, "");
//        builder.put(propertyName, orDefault.toString());
//      }
//      return builder.build();
////            return Settings.builder().put(this.properties).build();
//    }
//    return Settings.EMPTY;
///*       return Settings.builder()
//                .put("cluster.name",clusterName)
//               .put("client.transport.sniff", clientTransportSniff)
//               .put("client.transport.ignore_cluster_name",clientIgnoreClusterName)
//               .put("client.transport.ping_timeout", clientPingTimeout)
//                .put("client.transport.nodes_sampler_interval",clientNodesSamplerInterval)
//                .build();*/
//  }
//
//  private Set<InetSocketAddress> parseHostAndPort() throws Exception {
//    if (clusterNodes == null || clusterNodes.trim().isEmpty()) {
//      log.error("ES clusterNodes[ip:port] 配置不能为空");
//      throw new IllegalArgumentException("ES clusterNodes[ip:port] 配置不能为空");
//    }
//
//    String[] split = clusterNodes.split(",");
//    final Set<String> sentinelHosts = new HashSet<>();
//    for (String v : split) {
//      if (StringUtil.isNotBlank(v)) {
//        sentinelHosts.add(v.trim());
//      }
//    }
//    if (sentinelHosts.isEmpty()) {
//      log.error("ES clusterNodes[ip:port] 配置不能为空");
//      throw new UnknownHostException("ES clusterNodes[ip:port] 配置不能为空");
//    }
//
//    final Set<InetSocketAddress> addrs = new HashSet<>();
//
//    for (String host : sentinelHosts) {
//      boolean isIpPort = p.matcher(host).matches();
//      if (!isIpPort) {
//        log.error("解析 ES clusterNodes[ip 或 port] 不合法");
//        throw new IllegalArgumentException("解析 ES clusterNodes[ip 或 port] 不合法");
//      }
//      String[] ipPortArr = host.split(":");
//      InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(ipPortArr[0]),
//          Integer.parseInt(ipPortArr[1]));
//      addrs.add(address);
//    }
//    return addrs;
//  }
//
//  @Override
//  public void destroy() throws Exception {
//    try {
//      log.info("Closing elasticSearch client");
//      if (client != null) {
//        client.close();
//      }
//    } catch (final Exception e) {
//      log.error("Error closing ElasticSearch client: ", e);
//    }
//  }
//
//  @Override
//  public TransportClient getObject() throws Exception {
//    return client;
//  }
//
//  @Override
//  public Class<TransportClient> getObjectType() {
//    return TransportClient.class;
//  }
//
//  @Override
//  public boolean isSingleton() {
//    return true;
//  }
//
//}
