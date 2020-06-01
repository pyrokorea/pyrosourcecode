package rdr.apimsg;

import org.json.simple.JSONObject;

public class DomainItem 
{
	private String domain;
	
	private String desc;
	
	private String reasoner;
	
	public DomainItem()
	{
		;
	}
	
	public void set(String pDomain, String pDesc, String pReasoner)
	{
		domain = pDomain;
		desc = pDesc;
		reasoner = pReasoner;
	}
	
	public JSONObject getJSON()
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("domain", domain);
		jsonObj.put("desc", desc);
		jsonObj.put("reasoner", reasoner);
		return jsonObj;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @return the reasoner
	 */
	public String getReasoner() {
		return reasoner;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @param reasoner the reasoner to set
	 */
	public void setReasoner(String reasoner) {
		this.reasoner = reasoner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((reasoner == null) ? 0 : reasoner.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainItem other = (DomainItem) obj;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (reasoner == null) {
			if (other.reasoner != null)
				return false;
		} else if (!reasoner.equals(other.reasoner))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DomainItem [domain=" + domain + ", desc=" + desc + ", reasoner=" + reasoner + "]";
	}
	
}
