package models;

public enum IssueLinkType {
    TEST_BY;
    static{
        TEST_BY.setId("10534");
        TEST_BY.setInward("Is a test by");
        TEST_BY.setName("Is a test for");
        TEST_BY.setOutward("Is a test for");
        TEST_BY.setSelf("https://greenhopper.app.alcatel-lucent.com/rest/api/2/issueLinkType/10534");
    }
    
    private String id;
    private String name;
    private String inward;
    private String outward;
    private String self;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInward() {
        return inward;
    }

    public void setInward(String inward) {
        this.inward = inward;
    }

    public String getOutward() {
        return outward;
    }

    public void setOutward(String outward) {
        this.outward = outward;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

}
