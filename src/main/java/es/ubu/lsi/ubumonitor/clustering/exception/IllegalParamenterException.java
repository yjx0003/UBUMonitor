package es.ubu.lsi.ubumonitor.clustering.exception;

import java.text.MessageFormat;

import es.ubu.lsi.ubumonitor.clustering.data.ClusteringParameter;
import es.ubu.lsi.ubumonitor.controllers.I18n;

/**
 * Excepción de un valor inválido para un parámetro del algoritmo.
 * 
 * @author Xing Long Ji
 *
 */
public class IllegalParamenterException extends IllegalArgumentException {

	private static final long serialVersionUID = 7926694236793826773L;

	private final ClusteringParameter parameter;
	private final Number wrong;

	/**
	 * Constructor.
	 * 
	 * @param parameter parámetro
	 * @param wrong     valor incorrecto
	 */
	public IllegalParamenterException(ClusteringParameter parameter, Number wrong) {
		this.parameter = parameter;
		this.wrong = wrong;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMessage() {
		return MessageFormat.format(I18n.get("clustering.error.parameter"), parameter.getName(), wrong,
				parameter.getMin());
	}

}
