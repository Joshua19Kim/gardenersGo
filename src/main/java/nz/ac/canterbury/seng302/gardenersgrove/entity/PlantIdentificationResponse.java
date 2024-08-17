package nz.ac.canterbury.seng302.gardenersgrove.entity;

import java.util.List;

public class PlantIdentificationResponse {

    private Query query;
    private String language;
    private String preferedReferential;
    private String switchToProject;
    private String bestMatch;
    private List<Result> results;
    private int remainingIdentificationRequests;
    private String version;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPreferedReferential() {
        return preferedReferential;
    }

    public void setPreferedReferential(String preferedReferential) {
        this.preferedReferential = preferedReferential;
    }

    public String getSwitchToProject() {
        return switchToProject;
    }

    public void setSwitchToProject(String switchToProject) {
        this.switchToProject = switchToProject;
    }

    public String getBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(String bestMatch) {
        this.bestMatch = bestMatch;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public int getRemainingIdentificationRequests() {
        return remainingIdentificationRequests;
    }

    public void setRemainingIdentificationRequests(int remainingIdentificationRequests) {
        this.remainingIdentificationRequests = remainingIdentificationRequests;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class Query {
        private String project;
        private List<String> images;
        private List<String> organs;
        private boolean includeRelatedImages;
        private boolean noReject;

        public String getProject() {
            return project;
        }

        public void setProject(String project) {
            this.project = project;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<String> getOrgans() {
            return organs;
        }

        public void setOrgans(List<String> organs) {
            this.organs = organs;
        }

        public boolean isIncludeRelatedImages() {
            return includeRelatedImages;
        }

        public void setIncludeRelatedImages(boolean includeRelatedImages) {
            this.includeRelatedImages = includeRelatedImages;
        }

        public boolean isNoReject() {
            return noReject;
        }

        public void setNoReject(boolean noReject) {
            this.noReject = noReject;
        }
    }

    public static class Result {
        private double score;
        private Species species;
        private List<Image> images;
        private Gbif gbif;
        private Powo powo;
        private Iucn iucn;

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public Species getSpecies() {
            return species;
        }

        public void setSpecies(Species species) {
            this.species = species;
        }

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public Gbif getGbif() {
            return gbif;
        }

        public void setGbif(Gbif gbif) {
            this.gbif = gbif;
        }

        public Powo getPowo() {
            return powo;
        }

        public void setPowo(Powo powo) {
            this.powo = powo;
        }

        public Iucn getIucn() {
            return iucn;
        }

        public void setIucn(Iucn iucn) {
            this.iucn = iucn;
        }
    }

    public static class Species {
        private String scientificNameWithoutAuthor;
        private String scientificNameAuthorship;
        private String scientificName;
        private String genus;
        private String family;
        private List<String> commonNames;

        public String getScientificNameWithoutAuthor() {
            return scientificNameWithoutAuthor;
        }

        public void setScientificNameWithoutAuthor(String scientificNameWithoutAuthor) {
            this.scientificNameWithoutAuthor = scientificNameWithoutAuthor;
        }

        public String getScientificNameAuthorship() {
            return scientificNameAuthorship;
        }

        public void setScientificNameAuthorship(String scientificNameAuthorship) {
            this.scientificNameAuthorship = scientificNameAuthorship;
        }

        public String getScientificName() {
            return scientificName;
        }

        public void setScientificName(String scientificName) {
            this.scientificName = scientificName;
        }

        public String getGenus() {
            return genus;
        }

        public void setGenus(String genus) {
            this.genus = genus;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public List<String> getCommonNames() {
            return commonNames;
        }

        public void setCommonNames(List<String> commonNames) {
            this.commonNames = commonNames;
        }
    }

    public static class Image {
        private String organ;
        private String author;
        private String license;
        private Date date;
        private Url url;

        public String getOrgan() {
            return organ;
        }

        public void setOrgan(String organ) {
            this.organ = organ;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Url getUrl() {
            return url;
        }

        public void setUrl(Url url) {
            this.url = url;
        }
    }

    public static class Date {
        private long timestamp;
        private String string;

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    public static class Url {
        private String o;
        private String m;
        private String s;

        public String getO() {
            return o;
        }

        public void setO(String o) {
            this.o = o;
        }

        public String getM() {
            return m;
        }

        public void setM(String m) {
            this.m = m;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }
    }

    public static class Gbif {
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class Powo {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Iucn {
        private String id;
        private String category;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
