package be.vrt.services.log.exposer.es;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.EsExecutors;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.vrt.services.log.exposer.util.FileSystemUtils;

/*
 * Source: https://github.com/tlrx/elasticsearch-test/blob/master/src/main/java/com/github/tlrx/elasticsearch/test/provider/LocalClientProvider.java
 */
public class InMemoryMongo {

	private static final String DATA_PATH = "/tmp/target/elasticsearch/data";
	private Node node;
	private Client client;
	private ObjectMapper objectMapper = new ObjectMapper();

	public InMemoryMongo() {
	}

	public InMemoryMongo start() {
		node = NodeBuilder.nodeBuilder()
				//.loadConfigSettings(false)
				.local(true).data(true).settings(Settings.builder()
				.put(ClusterName.SETTING, "loggingTestCluster")
				.put("node.name", "logging2TestNode")
				.put(IndexMetaData.SETTING_NUMBER_OF_SHARDS, 1)
				.put(IndexMetaData.SETTING_NUMBER_OF_REPLICAS, 0)
				.put("discovery.zen.ping.multicast", "false")
				.put(EsExecutors.PROCESSORS, 1) // limit the number of threads created
				.put("http.enabled", false)
				.put("index.store.type", "default")
				.put("gateway.type", "default")
				.put("path.data", DATA_PATH)
				.put("path.home", DATA_PATH)
		).build();
		node.start();
		client = node.client();
		return this;
	}

	public Client getClient() {
		return client;
	}

	public void stop() {
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
				.create(new CreateIndexRequest(indexName).source(indexMapping))
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
		return Arrays.asList(indexResult.indices()).contains(indexName);
	}

	public void index(String indexName, String type, String... entryResources) {
		for (String entryResource : entryResources) {
			client.index(new IndexRequest(indexName, type).source(FileSystemUtils.read(getResource(entryResource)))).actionGet();
		}
	}

	public void index(String indexName, String type, EntryWithId... entries) {
		for (EntryWithId entry : entries) {
			try {
				String source = objectMapper.writeValueAsString(entry);
				client.index(new IndexRequest(indexName, type).id(entry.getId()).source(source)).actionGet();
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
