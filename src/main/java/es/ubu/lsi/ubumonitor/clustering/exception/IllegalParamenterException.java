package es.ubu.lsi.ubumonitor.clustering.exception;

import java.text.MessageFormat;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.controllers.I18n;

public class IllegalParamenterException extends IllegalArgumentException {

	private static final long serialVersionUID = 7926694236793826773L;

	private ClusteringParameter parameter;
	private Object wrong;

	public IllegalParamenterException(ClusteringParameter parameter, Object wrong) {
		this.parameter = parameter;
		this.wrong = wrong;
	}

	@Override
	public String getMessage() {
		return MessageFormat.format(I18n.get("clustering.error.parameter"), parameter.getName(), wrong,
				parameter.getMin());
	}

}