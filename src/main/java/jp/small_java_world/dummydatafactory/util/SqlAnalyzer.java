package jp.small_java_world.dummydatafactory.util;

import java.util.HashMap;
import java.util.Map;

import jp.small_java_world.dummydatafactory.config.ColumnTypeConfig;
import jp.small_java_world.dummydatafactory.data.SqlColumnData;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;

public class SqlAnalyzer {
	public static Map<String, SqlColumnData> getSqlColumnDataMap(String sqlContent) throws JSQLParserException {
		//キー:カラムに対応するJavaのクラスのメンバ名、値:カラムに対応するSqlColumnData
		Map<String, SqlColumnData> result = new HashMap<>();
		
		//create tableのsqlContentを解析
		CreateTable createTable = (CreateTable) CCJSqlParserUtil.parse(sqlContent);

		//解析結果からcolumnDefinitionsを取り出す。
		for (var columnDefinition : createTable.getColumnDefinitions()) {
			SqlColumnData sqlColumnData = new SqlColumnData();

			//javaTypeはcolumnType.ymlに定義してある設定で変換してセット
			var javaType = ColumnTypeConfig.getJavaType(columnDefinition.getColDataType().getDataType());
			sqlColumnData.setJavaType(javaType);
			sqlColumnData.setDbDataType(columnDefinition.getColDataType().getDataType());
			sqlColumnData.setColumnName(columnDefinition.getColumnName());
		
			//Javaのクラスのメンバ名はテーブルのカラム名をキャメルケースに変換してセット
			sqlColumnData.setColumnCamelCaseName(StringConvertUtil.toSnakeCaseCase(columnDefinition.getColumnName()));

			//カラムサイズをセット
			var argumentsStringList = columnDefinition.getColDataType().getArgumentsStringList();
			if (argumentsStringList != null) {
				sqlColumnData.setDbDataSize(Integer.parseInt(argumentsStringList.get(0)));
			}

			result.put(sqlColumnData.getColumnCamelCaseName(), sqlColumnData);
		}
		return result;
	}
}
