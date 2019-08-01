package report;

import java.util.ArrayList;
import java.util.List;

import foundation.callable.Callable;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.Util;
import foundation.variant.VariantSegment;
public class Chart extends Callable {
	
	private String BLANKSTR = "all";
	private String RES1 = "res1";
	private String RES2 = "res2";
	private String RES3 = "res3";
	private String RES4 = "res4";
	private String RES5 = "res5";
	private String RES6 = "res6";
	
	private String REPORT_TYPE1 = "businesskpi";
	private String REPORT_TYPE2 = "saleskpi";
	private String REPORT_TYPE3 = "distributorkpi";
	private String REPORT_TYPE4 = "terminalkpi";
	
	private String BOXTYPE_RSM = "RSM";
	private String BOXTYPE_SUPERVIOR = "Supervisor";
	private String BOXTYPE_SALES = "Salesperson";
	private String BOXTYPE_DISTRIBUTOR = "distributor";
	private String BOXTYPE_REGION = "region";

	protected void publishMethod() {
		addMethod("getChart");
		addMethod("getCheckbox");
	}
	
	protected void getCheckbox() throws Exception {
		//1.0 other filter
		String whereSQL = "";
		
		//1.3 user 
		String loginName = onlineUser.getName();
		String role = onlineUser.getRolecode();
		String englishName = onlineUser.getEnglishname();
		String reportCode = request.getParameter("reportCode");
		String boxtype = request.getParameter("boxtype");
		NamedSQL namedSQL = null;
		if (REPORT_TYPE3.equalsIgnoreCase(reportCode)){
			namedSQL = NamedSQL.getInstance("getkpichartfilterregion");
		} else {
			namedSQL = NamedSQL.getInstance("getCheckboxfilter");
		}
		
		if (RES1.equalsIgnoreCase(role) || RES6.equalsIgnoreCase(role)) {
			if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_RSM.equalsIgnoreCase(boxtype)){
				namedSQL.setParam("boxname", "所有大区经理");
				whereSQL = " and type = '" + BOXTYPE_RSM + "'";
			} else if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				namedSQL.setParam("boxname", "所有主管");	
				whereSQL = " and type = '" + BOXTYPE_SUPERVIOR + "'";
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				namedSQL.setParam("boxname", "所有主管");	
				whereSQL = " and type = '" + BOXTYPE_SUPERVIOR + "'";
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SALES.equalsIgnoreCase(boxtype)){
				namedSQL.setParam("boxname", "所有销售");	
				whereSQL = " and type = '" + BOXTYPE_SALES + "'";
			} else if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_REGION.equalsIgnoreCase(boxtype)){
				namedSQL.setParam("boxname", "所有区域");	
			} else if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_DISTRIBUTOR.equalsIgnoreCase(boxtype)){
				namedSQL = NamedSQL.getInstance("getkpichartfilterdistributor");
				namedSQL.setParam("boxname", "所有经销商");	
			} else if (REPORT_TYPE4.equalsIgnoreCase(reportCode) && BOXTYPE_RSM.equalsIgnoreCase(boxtype)){
				namedSQL.setParam("boxname", "所有大区经理");
				whereSQL = " and type = '" + BOXTYPE_RSM + "'";	
			}
		} else if (RES5.equalsIgnoreCase(role)){
			if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_RSM.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有大区经理");
			} else if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				whereSQL = " and ParentLoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有主管");	
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				whereSQL = " and ParentLoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有主管");	
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SALES.equalsIgnoreCase(boxtype)){
				whereSQL = " and ParentLoginName in(select loginname from organization where ParentLoginName= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有销售");	
			} else if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_REGION.equalsIgnoreCase(boxtype)){
				whereSQL = " and Region in(SELECT Region FROM DistributorHierarchy where rsm= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有区域");	
			} else if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_DISTRIBUTOR.equalsIgnoreCase(boxtype)){
				namedSQL = NamedSQL.getInstance("getkpichartfilterdistributor");
				whereSQL = " and distributorcode in(SELECT distributorcode FROM DistributorHierarchy where rsm= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有经销商");	
			} else if (REPORT_TYPE4.equalsIgnoreCase(reportCode) && BOXTYPE_RSM.equalsIgnoreCase(boxtype)){
				whereSQL = " and loginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有大区经理");
			}	
		}else if (RES4.equalsIgnoreCase(role)){
			if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_RSM.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName in(select ParentLoginName from organization where LoginName= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有大区经理");
			} else if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有主管");	
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有主管");	
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SALES.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName in(select loginname from organization where ParentLoginName= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有销售");	
			} else if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_REGION.equalsIgnoreCase(boxtype)){
				whereSQL = " and Region in(select Region from DistributorHierarchy where Supervisor= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有区域");	
			} else if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_DISTRIBUTOR.equalsIgnoreCase(boxtype)){
				namedSQL = NamedSQL.getInstance("getkpichartfilterdistributor");
				whereSQL = " and distributorcode in(SELECT distributorcode FROM DistributorHierarchy where Supervisor= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有经销商");	
			}
		}else if (RES3.equalsIgnoreCase(role)){
			if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_RSM.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", englishName);
			} else if (REPORT_TYPE1.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				whereSQL = " and ParentLoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有主管");	
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SUPERVIOR.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName in (select ParentLoginName from organization where LoginName= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有主管");	
			} else if (REPORT_TYPE2.equalsIgnoreCase(reportCode) && BOXTYPE_SALES.equalsIgnoreCase(boxtype)){
				whereSQL = " and LoginName = '" + loginName + "'";
				namedSQL.setParam("boxname", "所有销售");	
			}
		}else if (RES2.equalsIgnoreCase(role)){
			if (REPORT_TYPE3.equalsIgnoreCase(reportCode) && BOXTYPE_DISTRIBUTOR.equalsIgnoreCase(boxtype)){
				namedSQL = NamedSQL.getInstance("getkpichartfilterdistributor");
				whereSQL = " and distributorcode in (SELECT distributorcode FROM DistributorHierarchy where distributorcode= '" + loginName + "')";
				namedSQL.setParam("boxname", "所有经销商");	
			}
		}
		String otherFilter = request.getParameter("filter1");
		if (Util.isEmptyStr(otherFilter)) {
			otherFilter = "1=1";
		} 
		otherFilter += whereSQL;
		//1.5 get data
		namedSQL.setFilter(otherFilter);
		
		for (VariantSegment variant : namedSQL) {
			if (variant.isEmpty()) {
				String value = request.getParameter(variant.getName());
				variant.setValue(value);
			}
		}
		
		EntitySet entitySet = SQLRunner.getEntitySet(namedSQL);
		resultPool.addValue("rows",entitySet);
		resultPool.success();
		
	}

	protected void getChart() throws Exception {
		List<Entity> resultList = new ArrayList<Entity>();
		NamedSQL namedSQL = null; 
		
		//2. get sale out amount
		String sqlName = request.getParameter("sqlname");
		namedSQL = NamedSQL.getInstance(sqlName);
				
		for (VariantSegment variant : namedSQL) {
			String name = variant.getName();
			String value = locateSQLVariant(name);
			
			if (name.equalsIgnoreCase("filter") && Util.isEmptyStr(value)) {
				value = "1=1";
			}
			variant.setValue(value);
		}
		
		EntitySet entitySet = SQLRunner.getEntitySet(namedSQL);
		/*TableMeta tableMeta = entitySet.getTableMeta();
		Entity entity4Total = new Entity(tableMeta);
		entity4Total.set("Category", "指标");*/
		for (Entity entity : entitySet) {
			resultList.add(entity);
		}
		
		//1. get target by login name
		String tableName = null, categoryFilter, yearFilter;
		
		String reportCode = request.getParameter("reportCode");
		String tabCode = request.getParameter("tabCode");
		
		if (reportCode == null) {
			logger.error("execute procedure error, empty param: " + reportCode);
		}
		
		//1.0 other filter
		String otherFilter = request.getParameter("filter");
		if (Util.isEmptyStr(otherFilter)) {
			otherFilter = "1=1";
		}
		
		//1.1 year
		yearFilter = "Year = '" + request.getParameter("Year") + "'";
		//1.2 category
				if (tabCode.equalsIgnoreCase("Anthogyr")) {
					categoryFilter = "Category = '总销售额' and brand = 'Anthogyr'";
				} else if (tabCode.equalsIgnoreCase("Straumann")) {
					categoryFilter = "Category = '总销售额' and brand = 'Straumann'";
				} else if (tabCode.equalsIgnoreCase("total")) {
					categoryFilter = "Category = '总销售额' and brand = 'Straumann'";
				} else if (tabCode.equalsIgnoreCase("roxolid")) {
					categoryFilter = "Category = 'Roxolid' and brand = 'Straumann'";
				} else if (tabCode.equalsIgnoreCase("terminal")) {
					categoryFilter = "Category = '总数量' and RSM <> ''";
				}else if (tabCode.equalsIgnoreCase("A_total")) {
					categoryFilter = "Category = '总销售额' and brand = 'Anthogyr'";
				}else if (tabCode.equalsIgnoreCase("A_roxolid")) {
					categoryFilter = "Category = 'Roxolid' and brand = 'Anthogyr'";
				}
				else {
					categoryFilter = "Category = '总销售额'";
				}
		
		//1.3 user 
		String loginName = onlineUser.getName();
		
		//for home page
		String type = onlineUser.getType();
		String rsm = dataPool.getParameter("rsm").getStringValue();
		String supervisor = dataPool.getParameter("supervisor").getStringValue();
		String area = dataPool.getParameter("area").getStringValue();
		String csf = dataPool.getParameter("csf").getStringValue();
		String distributorcode = dataPool.getParameter("distributorcode").getStringValue();
		if (reportCode.equalsIgnoreCase("businesskpi")) {
			tableName = "SupervisorTargets";
			if (loginName.equalsIgnoreCase("superadmin") || loginName.equalsIgnoreCase("admin") || type.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin")){
				if (BLANKSTR.equals(rsm) && BLANKSTR.equals(supervisor)){
					otherFilter = " SupervisorID = 'U108272'" ;
				} else if (BLANKSTR.equals(rsm) && !BLANKSTR.equals(supervisor)){
					otherFilter = " SupervisorID = '" + supervisor + "'";
				} else if (!BLANKSTR.equals(rsm) && !BLANKSTR.equals(supervisor)){
					otherFilter = " SupervisorID = '" + supervisor + "'";
				}else {
					otherFilter = " SupervisorID = '" + rsm + "'";
				}
			} else {
				if (BLANKSTR.equals(rsm) && BLANKSTR.equals(supervisor)){
					otherFilter = " SupervisorID = '" + loginName + "'";
				} else if (BLANKSTR.equals(rsm) && !BLANKSTR.equals(supervisor)){
					otherFilter = " SupervisorID = '" + supervisor + "'";
				} else if (!BLANKSTR.equals(rsm) && !BLANKSTR.equals(supervisor)){
					otherFilter = " SupervisorID = '" + supervisor + "'";
				}else {
					otherFilter = " SupervisorID = '" + rsm + "'";
				}
			}
			
		} else if (reportCode.equalsIgnoreCase("saleskpi")) {
			tableName = "SaleTargets";
			if (loginName.equalsIgnoreCase("superadmin") || loginName.equalsIgnoreCase("admin") || type.equalsIgnoreCase("superadmin")){
				if (BLANKSTR.equals(supervisor) && BLANKSTR.equals(csf)){
					otherFilter = " SaleCode = 'U108272'" ;
				} else if (BLANKSTR.equals(supervisor) && !BLANKSTR.equals(csf)){
					otherFilter = " SaleCode = '" + csf + "'";
				} else if (!BLANKSTR.equals(supervisor) && !BLANKSTR.equals(csf)){
					otherFilter = " SaleCode = '" + csf + "'";
				}else {
					tableName = "SupervisorTargets";
					otherFilter = " SupervisorID = '" + supervisor + "'";
				}
			} else {
				if (BLANKSTR.equals(supervisor) && BLANKSTR.equals(csf)){
					otherFilter = " SaleCode = '" + loginName + "'";
				} else if (BLANKSTR.equals(supervisor) && !BLANKSTR.equals(csf)){
					otherFilter = " SaleCode = '" + csf + "'";
				} else if (!BLANKSTR.equals(supervisor) && !BLANKSTR.equals(csf)){
					otherFilter = " SaleCode = '" + csf + "'";
				}else {
					tableName = "SupervisorTargets";
					otherFilter = " SupervisorID = '" + supervisor + "'";
				}
			}
		} else if (reportCode.equalsIgnoreCase("distributorkpi")) {
			tableName = "VIEW_Distributor_SYTarget";
			if (loginName.equalsIgnoreCase("superadmin") || loginName.equalsIgnoreCase("admin") || type.equalsIgnoreCase("admin")  || type.equalsIgnoreCase("superadmin")){
				if (BLANKSTR.equals(area) && BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = 'admin'" ;
				} else if (BLANKSTR.equals(area) && !BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = '" + distributorcode + "'";
				} else if (!BLANKSTR.equals(area) && !BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = '" + distributorcode + "'";
				}else if (!BLANKSTR.equals(area) && BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = '" + area + "'";
				}else {
					otherFilter = " DistributorCode = 'admin'";
				}
			} else {
				if (BLANKSTR.equals(area) && BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = '" + loginName + "'";
				} else if (BLANKSTR.equals(area) && !BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = '" + distributorcode + "'";
				} else if (!BLANKSTR.equals(area) && !BLANKSTR.equals(distributorcode)){
					otherFilter = " DistributorCode = '" + distributorcode + "'";
				}else {
					otherFilter = " DistributorCode = '" + loginName + "'";
				}
			}
		} else if (reportCode.equalsIgnoreCase("homekpi")) {
			tableName = "SupervisorTargets";
			if (loginName.equalsIgnoreCase("superadmin") || loginName.equalsIgnoreCase("admin") || type.equalsIgnoreCase("admin")  || type.equalsIgnoreCase("superadmin")){
					otherFilter = " SupervisorID = 'U108272'" ;
			} else {
					otherFilter = " SupervisorID = '" + loginName + "'";
			}
		} else if (reportCode.equalsIgnoreCase("terminalkpi")) {
			tableName = "TerminalTargets";
			if (loginName.equalsIgnoreCase("superadmin") || type.equalsIgnoreCase("superadmin") || type.equalsIgnoreCase("admin")){
				if (otherFilter.equalsIgnoreCase("1=1")) {
					otherFilter = "1 = 1 order by rsm desc" ;
				}
			} else {
					otherFilter = " Rsm = '" + loginName + "' order by rsm desc";
			}
		}
		
		//1.5 get data
		String filter = yearFilter + " and " + categoryFilter + " and " + otherFilter;
		
		namedSQL = NamedSQL.getInstance("getDataSet");
		namedSQL.setTableName(tableName);
		namedSQL.setFilter(filter);
		
		for (VariantSegment variant : namedSQL) {
			if (variant.isEmpty()) {
				String value = request.getParameter(variant.getName());
				variant.setValue(value);
			}
		}
		
		EntitySet targetSet = SQLRunner.getEntitySet(namedSQL);
		
		//1.6. update data
		for (Entity entity : targetSet) {
			if(!Util.isEmptyStr(entity)){
				String category = entity.getString("Category");
				Integer target = entity.getInteger("target");
				if (category.equalsIgnoreCase("Roxolid")) {
					int Target = target*1000;
					entity.set("target", Target);
				}
				entity.set("Category", "指标");
				resultList.add(entity);
			}
		}
		//Entity yearedBUDatas = targetSet.next();
		
		
		
		//3. return
		resultPool.addValue("rows", resultList);
	}
}
