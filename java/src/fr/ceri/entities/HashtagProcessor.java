package fr.ceri.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.search.SearchHit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashtagProcessor
{
    private Map<String, String> hashList;
    private String targetDataFile;
    private ObjectMapper mapper;

    public HashtagProcessor()
    {
        hashList = new HashMap<>();
        targetDataFile = "src/fr/ceri/data/annotated/hashtags.json";
        mapper = new ObjectMapper();
    }

    public Map<String, String> getHashList()
    {
        return hashList;
    }

    public String getTargetDataFile()
    {
        return targetDataFile;
    }

    public void convertToHashtagMap(List<SearchHit> hits, String field)
    {
        for (SearchHit hit : hits)
        {
            Object hashtags = hit.getSourceAsMap().get(field);

            if (hashtags != null)
            {
                JsonNode[] nodes = mapper.convertValue(hashtags, JsonNode[].class);

                for (JsonNode node : nodes)
                {
                    hashList.put(node.get("text").asText(), "");
                }
            }
        }
    }

    public void displayHashtagMap()
    {
        System.out.println("-- Displaying " + hashList.size() + " hashtags --");

        for (Map.Entry<String, String> entry : hashList.entrySet())
        {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }
}