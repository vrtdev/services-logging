package be.vrt.services.logging.log.common.dto;

public class ForkFlowDto {

	private String parentFlowId;
	private String childFlowId;

	public String getParentFlowId() {
		return parentFlowId;
	}

	public void setParentFlowId(String parentFlowId) {
		this.parentFlowId = parentFlowId;
	}

	public String getChildFlowId() {
		return childFlowId;
	}

	public void setChildFlowId(String childFlowId) {
		this.childFlowId = childFlowId;
	}
	
	
	
}
