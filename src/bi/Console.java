package bi;

import bi.agg.Operator;
import bi.exception.AggBaseException;
import com.google.gson.Gson;
import foundation.callable.Callable;
import foundation.config.Configer;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.data.Page;
import foundation.persist.DataHandler;
import foundation.persist.Field;
import foundation.persist.TableMeta;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Console extends Callable {


	public static WorkEngine engine;
	private String operator;

	static {
        engine = WorkEngine.getInstance();

	}

	protected void doReceive(String[] paths) throws Exception {
		if (paths.length >= 2) {
			operator = paths[1];

			if ("agg".equalsIgnoreCase(operator)) {
			    //return;
				createBaseAggregation();
			}
			else if ("showMsgs".equalsIgnoreCase(operator)) {
				showMessages();
			}
			else if ("data".equalsIgnoreCase(operator)) {
				getAggData();
			}else if ("lasttime".equalsIgnoreCase(operator)) {
				getLastTime();
			} else if ("aggachieve".equalsIgnoreCase(operator)) {
			    aggAchieve();
            } else if ("refreshterritory".equalsIgnoreCase(operator)) {
			    refreshTerrtory();
            }else if ("getposition".equalsIgnoreCase(operator)) {
                getPosition();
            }


		}
		else {
            writer.ReplyError("bad bi console message path:" + fullPath);
        }
	}

    private void getPosition() {
        String userType = request.getParameter("userType");
        String parent = request.getParameter("parentField");
        String parentId = request.getParameter("parentId");
        String parentFilter = " 1 = 1";
        if (!Util.isNull(parent) && !Util.isNull(parentId)) {
            parentFilter = Util.quotedEqualStr(parent, parentId);
        }
        try {
            NamedSQL getPosition = NamedSQL.getInstance("getPosition");
            getPosition.setParam("userType", userType);
            getPosition.setParam("parentFilter", parentFilter);
            EntitySet entitySet = SQLRunner.getEntitySet(getPosition);
            resultPool.addValue("rows",entitySet);
            resultPool.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshTerrtory() {
        try {
            Util.changeData("Salesperson", "LoginName", "SuperAdmin");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void aggAchieve() {
        ThemeContext context = null;
        try {
            context = new ThemeContext(request, onlineUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Operator operator = new Operator(context);
        try {
            engine.exec(operator, "aggachieve");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getLastTime() {
        try {
            NamedSQL instance = NamedSQL.getInstance(AggConstant.lastDataTimeSql);
            ArrayList<String> DDITableList = AggConstant.getDDITableds();
            Date lastDate = null;
            for (String DDITableName : DDITableList) {
                instance.setParam(AggConstant.DDITable, DDITableName);
                Entity entity = SQLRunner.getEntity(instance);
                lastDate = entity.getDate(AggConstant.BizDate);
            }

            resultPool.addValue(AggConstant.BizDate, Util.DataTimeToString(lastDate));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAggData() {
        String userType = request.getParameter(AggConstant.userType);
        String userId = request.getParameter(AggConstant.userId);
        String fields = request.getParameter(AggConstant.fields);
        String code = request.getParameter(AggConstant.BI_Field_Code);
        String filter = request.getParameter(AggConstant.filter);
        boolean isTable = Util.stringToBoolean(request.getParameter("isTable"), false);
        String orderBy = request.getParameter("orderBy");
        String dataType = null;
        String aggCode = null;

       if ("superadmin".equalsIgnoreCase(userType)) {
            userId = Configer.getParam("adminCode");
        }

        if (Util.isNull(code) || Util.isNull(userType) || Util.isNull(userId)) {
            return;
        }

        if (Util.isNull(orderBy)) {
            orderBy = "";
        }

		if (Util.isNull(fields)) {
			if (AggConstant.achieve.equalsIgnoreCase(code)) {
                String sqlName = request.getParameter(AggConstant.dataName);
                aggCode = request.getParameter(AggConstant.BI_Field_Aggcode);
                dataType = request.getParameter(AggConstant.dataType);
                if (Util.isNull(sqlName)) {
                    return;
                } else {
                    fields = sqlName;
                }
                if (Util.isNull(dataType)) {
                    dataType = EAchieveDataType.unkonwn.name();
                }
            }
		}

        if (Util.isNull(filter)) {
            filter = Util.defaultFilter;
        }
        String resultSql = null;
        //1.?
        GroupSqlBulider bulider = new GroupSqlBulider(isTable);
		if (code.equalsIgnoreCase(AggConstant.achieve)) {
            String type = request.getParameter(AggConstant.BI_Field_Type);
            resultSql = bulider.getAchieveRegroupedSql(userId, userType, aggCode, type, fields, dataType, filter,orderBy);
		}else{
        	resultSql = bulider.getAggRegroupedSql(userId, userType, code, fields, filter);
        }
        logger.info(resultSql);
        if (Util.isNull(resultSql)) {
            throw new AggBaseException("计算sql错误");
        }
        try {
            NamedSQL resultNamedSql = new NamedSQL(AggConstant.agg,Util.String_Empty);
            resultNamedSql.setSql(resultSql);
            Date pre = new Date();
            EntitySet entitySet = SQLRunner.getEntitySet(resultNamedSql);
            Date now = new Date();
            logger.info(resultSql + " 耗时：" + (now.getTime() - pre.getTime()) / 1000);
            //是否需要分页
            if (isTable) {
                //分页
                combineTableData(entitySet);
            } else {
                combineResultSql(entitySet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void combineTableData(EntitySet entitySet) {
        List<Entity> entityList = entitySet.getEntityList();
        int count = entityList.size();
        Page page = getPage(count);
        int beginNo = page.getBeginRecordNo_1();
        int endNo = page.getEndRecordNo();

        List<Entity> collect = entityList.stream().skip(beginNo).limit(endNo).collect(Collectors.toList());

        resultPool.addValue("rows",collect);
        if (page != null) {
            resultPool.addValue("page", page);
            resultPool.addValue("total", page.getRecordCount());
        }
    }


    private void combineResultSql(EntitySet entitySet) {
        TableMeta tableMeta = entitySet.getTableMeta();
        List<Field> fields = tableMeta.getFields();
        Gson gson = new Gson();
        if (entitySet.size() == 0) {
            return;
        }
        Map<String, List<String>> resultMap = new HashMap<>();
        fields.stream().map(field -> resultMap.put(field.getName(), combineList(entitySet.getFieldList(field.getName())))).count();
        String jsonStr = gson.toJson(resultMap);
        resultPool.setJson(jsonStr);

    }

    private List<String> combineList(List<String> fieldList) {
        ArrayList<String> resultList = new ArrayList<>();
        for (String field : fieldList) {
            if (Util.isNull(field)) {
                field = Util.String_Empty;
            }
            resultList.add(combineOneData(field));
        }
        List<String> collect = resultList.stream().filter(s -> !Util.isNull(s)).collect(Collectors.toList());
        if (collect.size() == 0) {
            resultList = new ArrayList<>();
        }
        return resultList;
    }

    private String combineOneData(String string) {
        String result;
	    try {
            BigDecimal bigDecimal = new BigDecimal(string);
            BigDecimal setScaleDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
            result = setScaleDecimal.toString();
        } catch (Exception e) {
            result = string;
        }
	    return result;
    }


    /**
	 * @param month
	 * @param fiscalYear
	 * @return
	 * @throws Exception
	 */
	private Entity getPeroidLine(String month, String fiscalYear) throws Exception {
		EntitySet dataSet = DataHandler.getDataSet("peroid", "period  =" + Util.quotedStr(fiscalYear) + "and month = " + Util.quotedStr(month));
		Entity periodLine = dataSet.next();
		if (periodLine == null) {
			return null;
		}
		return periodLine;
	}

	private void showMessages() {
		resultPool.addValue(engine.getProgressor());
	}



	private void getMeta() {
		// TODO Auto-generated method stub

	}

	private void createBaseAggregation() throws Exception {
		ThemeContext context = new ThemeContext(request, onlineUser);
		Operator operator = new Operator(context);

		try {
			engine.exec(operator, "createbaseaggregation");

			resultPool.setMessage("200", "聚合任务提交成功");
		} catch (Exception e) {
			e.printStackTrace();
			resultPool.error("-1", "聚合失败");
		}
	}
}
