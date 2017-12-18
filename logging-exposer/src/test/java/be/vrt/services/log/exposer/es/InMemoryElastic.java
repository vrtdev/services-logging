package be.vrt.services.log.exposer.es;

import be.vrt.services.log.exposer.util.FileSystemUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.transport.Netty4Plugin;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;

/*
 * Source: https://github.com/tlrx/elasticsearch-test/blob/master/src/main/java/com/github/tlrx/elasticsearch/test/provider/LocalClientProvider.java
 */
public class InMemoryElastic {

	private static final String DATA_PATH = "./target/elasticsearch/data";
	private Node node;
	private Client client;
	private ObjectMapper objectMapper = new ObjectMapper();

	public InMemoryElastic() {
	}

	public InMemoryElastic start() throws NodeValidationException {
		node = new PluginConfigurableNode(getSettings(), asList(Netty4Plugin.class));
		node.start();
		client = node.client();
		return this;
	}

	private Settings getSettings(){
		Settings.Builder builder = Settings.builder()
				.put("cluster.name", "loggingTestCluster")
				.put("node.name", "loggingTestNode")
				.put("processors", 1)
				.put("transport.type", "netty4")
				.put("http.type", "netty4")
                .put("http.enabled", true)
                .put("http.port", 9201)
				.put("path.home", DATA_PATH)
				.put("path.data", DATA_PATH)
				.put("discovery.zen.ping_timeout", 0)
				.put("discovery.zen.ping.unicast.hosts", Collections.emptyList())
				.put("discovery.zen.minimum_master_nodes", 1)
				.put("network.host", "127.0.0.1");
		return builder.build();
	}

	private static class PluginConfigurableNode extends Node {
		PluginConfigurableNode(Settings settings, Collection<Class<? extends Plugin>> classpathPlugins) {
			super(InternalSettingsPreparer.prepareEnvironment(settings, null), classpathPlugins);
		}
	}

	public Client getClient() {
		return client;
	}

	public void stop() throws IOException {
		if(client != null) {
			client.close();
		}
		if(node != null && !node.isClosed()) {
			node.close();
			FileSystemUtils.deleteRecursivly(Paths.get(DATA_PATH));
		}
	}

	public void createIndex(String indexName, String mappingResource) {
		String indexMapping = FileSystemUtils.read(getResource(mappingResource));
		client.admin()
				.indices()
				.create(new CreateIndexRequest(indexName).source(indexMapping, XContentType.JSON))
				.actionGet();
	}

	public void reCreateIndex(String indexName, String mappingResource) {
		if(indexExists(indexName)) {
			client.admin().indices().delete(new DeleteIndexRequest(indexName));
		}
		createIndex(indexName, mappingResource);
	}

	private boolean indexExists(String indexName) {
		GetIndexResponse indexResult = client.admin().indices().getIndex(new GetIndexRequest()).actionGet();
		return asList(indexResult.indices()).contains(indexName);
	}

	public void index(String indexName, String type, String... entryResources) {
		for (String entryResource : entryResources) {
			client.index(new IndexRequest(indexName, type).source(FileSystemUtils.read(getResource(entryResource)), XContentType.JSON)).actionGet();
		}
	}

	public void index(String indexName, String type, EntryWithId... entries) {
		for (EntryWithId entry : entries) {
			try {
				String source = objectMapper.writeValueAsString(entry);
				client.index(new IndexRequest(indexName, type).id(entry.getId()).source(source, XContentType.JSON)).actionGet();
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private URL getResource(String mappingResource) {
		return getClass().getClassLoader().getResource(mappingResource);
	}

	public void flush(String indexName){
		client.admin().indices().flush(new FlushRequest(indexName)).actionGet();
	}

	public interface EntryWithId {
		String getId();
	}
}
