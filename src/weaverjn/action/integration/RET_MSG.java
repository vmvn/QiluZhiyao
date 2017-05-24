package weaverjn.action.integration;

public class RET_MSG {
	private String MSG_TYPE;
	private String MESSAGE;
	private String partnerid;
	public String getMSG_TYPE() {
		return MSG_TYPE;
	}
	
	public void setMSG_TYPE(String MSG_TYPE) {
		this.MSG_TYPE = MSG_TYPE;
	}
	
	public String getMESSAGE() {
		return MESSAGE;
	}
	
	public void setMESSAGE(String MESSAGE) {
		this.MESSAGE = MESSAGE;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}
}
