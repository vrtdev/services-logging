package be.vrt.services.log.collector.transaction.filter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class StatusServletResponse extends HttpServletResponseWrapper {
	private int status;

	public StatusServletResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void setStatus(int sc) {
		this.status = sc;
		super.setStatus(sc);
	}

	public int getStatus() {
		return status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StatusServletResponse) {
			StatusServletResponse response = (StatusServletResponse) obj;
			return this.getResponse().equals(response.getResponse());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getResponse().hashCode();
	}

}
