package be.vrt.services.log.exposer.es.result;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ElasticSearchCountResultTest {

    public static final String VALUE_1 = "aValue1";
    public static final String KEY_1 = "aKey1";

    @Test
    public void from_whenNoAggregations_thenResultIsEmpty(){
        HashMap<String, Object> data = new HashMap<>();
        data.put(KEY_1, new Object());
        ElasticSearchCountResult elasticSearchCountResult = ElasticSearchCountResult.from(data);

        Assert.assertTrue(elasticSearchCountResult.getAggregations().isEmpty());
    }

    @Test
    public void from_whenAggregations_thenResultIsNotEmpty(){
        HashMap<String, Object> value = new HashMap<>();
        value.put(KEY_1, VALUE_1);
        HashMap<String, Object> data = new HashMap<>();
        data.put("aggregations", value);

        ElasticSearchCountResult elasticSearchCountResult = ElasticSearchCountResult.from(data);

        Assert.assertEquals(VALUE_1, elasticSearchCountResult.getAggregations().get(KEY_1));
    }

    @Test
    public void empty_whenCalled_thenAggregationsAreEmpty(){
        Assert.assertTrue(ElasticSearchCountResult.empty().getAggregations().isEmpty());
    }

}