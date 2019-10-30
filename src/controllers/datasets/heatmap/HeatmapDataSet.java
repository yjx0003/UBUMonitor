package controllers.datasets.heatmap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import controllers.datasets.DataSet;
import controllers.ubulogs.GroupByAbstract;
import model.EnrolledUser;
import util.UtilMethods;

public class HeatmapDataSet<T> {

	public String createSeries(List<EnrolledUser> enrolledUsers, List<EnrolledUser> selectedUsers,
			List<T> selecteds, GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd, DataSet<T> dataSet) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		Map<EnrolledUser, Map<T, List<Long>>> userCounts = dataSet.getUserCounts(groupBy, enrolledUsers, selecteds,
				dateStart, dateEnd);
		List<String> rangeDates = groupBy.getRangeString(dateStart, dateEnd);

		for (int i = selectedUsers.size() - 1; i >= 0; i--) {
			EnrolledUser selectedUser = selectedUsers.get(i);
			if (selectedUser == null)
				continue;
			stringBuilder.append("{name:'" + UtilMethods.escapeJavaScriptText(selectedUser.toString()) + "',");

			Map<T, List<Long>> types = userCounts.get(selectedUser);
			List<Long> results = new ArrayList<>();
			for (int j = 0; j < rangeDates.size(); j++) {
				long result = 0;
				for (T type : selecteds) {
					List<Long> times = types.get(type);
					result += times.get(j);
				}
				results.add(result);
			}
			stringBuilder.append("data: [" + UtilMethods.join(results) + "]},");

		}
		stringBuilder.append("]");

		return stringBuilder.toString();
	}

	public static String createCategory(GroupByAbstract<?> groupBy, LocalDate dateStart, LocalDate dateEnd) {
		return "[" + UtilMethods.joinWithQuotes(groupBy.getRangeString(dateStart, dateEnd)) + "]";
	}

}
