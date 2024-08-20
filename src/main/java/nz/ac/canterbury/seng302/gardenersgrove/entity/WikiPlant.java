package nz.ac.canterbury.seng302.gardenersgrove.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="wikiPlants")
public class WikiPlant {
    @JsonProperty("data")
    private JsonNode data;

    @Id
    private long id;
    @Column
    private String name;
    @Column(name = "scientific_name")
    private String scientificName;

    public WikiPlant() {

    }
    public JsonNode getData() {return data;}

    public void setData(JsonNode data) {
        this.id = data.get("id").asLong();
        this.name = data.get("common_name").asText();
        this.scientificName = data.get("scientific_name").asText();
    }

    public String getName() {return name;}

    public String getScientificName() {return scientificName;}
}
