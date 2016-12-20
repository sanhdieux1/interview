package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JQLIssuefields {
	private JQLIssuetypeVO issuetype;

	public JQLIssuetypeVO getIssuetype() {
		return issuetype;
	}

	public void setIssuetype(JQLIssuetypeVO issuetype) {
		this.issuetype = issuetype;
	}
	
}
