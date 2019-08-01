package bi;

import bi.define.AggDirection;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

public class AggConstant {

    public static final String BI_TABLE_THEMEGROUP = "bi_themegroup";
    public static final String BI_TABLE_MAINDATATABLE = "bi_maindatatable";
    public static final String BI_Field_MainData = "maindata";
    public static final String BI_Field_MainTableId = "maintableid";
    public static final String BI_Field_MapField = "mapfield";
    public static final String BI_Field_DimensionMainCode = "dimensionmaincode";

    public static final String BI_Filter_Active = "active = 'T'";
    public static final String BI_Field_No = "no";
    public static final String BI_Field_Id = "id";
    public static final String BI_Field_Code = "code";
    public static final String BI_Field_Name = "name";
    public static final String BI_Field_Type = "type";
    public static final String BI_Field_Active = "active";
    public static final String BI_Field_Topictype = "topictype";
    public static final String BI_Field_Aggcode = "aggcode";
    public static final String BI_Field_Flowid = "flowid";

    public static final String BI_TABLE_THEMEGROUPMAP = "bi_themegroupmap";

    public static final String BI_Field_ThemeGroupId = "themegroupid";
    public static final String BI_Field_RealTable = "realtable";
    public static final String BI_Field_DimensionGroups = "dimensiongroups";
    public static final String BI_Field_Measurmentgroups = "measurmentgroups";


    public static final String BI_TABLE_THEMEG = "bi_theme";
    public static final String BI_Theme_DefaultName = "@{dimension}-@{measurment}聚合";
    public static final String BI_Field_Measurment = "measurment";
    public static final String BI_Field_Dimension = "dimension";

    public static final String BI_TABLE_SqlTemplate = "bi_sqltemplate";
    public static final String BI_Field_Sqltemplateid = "sqltemplateid";
    public static final String BI_Field_Targettable = "targettable";
    public static final String BI_Field_Sourcetable = "sourcetable";
    public static final String BI_Field_Sql = "sql";

    public static final String BI_SqlTemplate_DefaultAchieveSqlId = "Achieve-Insert";
    public static final String BI_SqlTemplate_DefaultGowthSqlId = "Gowth-Update";
    public static final String BI_SqlTemplate_DefaultRank_InsertSqlId = "Rank_Insert";

    public static final String BI_TABLE_Dimension = "bi_dimension";
    public static final String BI_Field_GroupId = "groupid";

    public static final String BI_TABLE_Measurment = "bi_measurment";


    public static final String BI_TABLE_TABLE = "bi_table";
    public static final String BI_TABLE_FLEID = "bi_field";

    public static final String BI_TABLE_TDIMENSIONGROUP = "bi_dimensiongroup";
    public static final String BI_TABLE_Fieldname = "fieldname";
    public static final String BI_TABLE_Parentcode = "parentcode";


    public static final String Sql_createTableTemplate = "createTableTemplate";
    public static final String Sql_createAggPartitionTableTemplate = "createAggPartitionTableTemplate";
    public static final String Sql_Field_tableName = "tableName";
    public static final String Sql_Field_fields = "fields";
    public static final String Sql_getDimensionByCodeGroupId = "getDimensionByCodeGroupId";
    public static final String Sql_getMeasurmentByCode = "getMeasurmentByCode";

    public static final String Create_CommonField_Template = "[{0}] nvarchar({1}) NULL";
    public static final String Create_DateField_Template = "[{0}] date NULL";
    public static final String Create_IntField_Template = "[{0}] int NULL";
    public static final String Create_Primary_Template = " PRIMARY KEY ";
    public static final int CommonField_Default_Length = 64;
    public static final String Create_AggField_Template = "[{0}] decimal({1},{2}) NULL";
    public static final int AggField_Default_Length = 20;
    public static final int AggField_Default_decimalLength = 3;


    public static final String BI_Default_TopicView = "VIEW_Topic_";
    public static final String BI_Default_ThemeGroup = "TG_";
    public static final String BI_Default_Agg = "agg_";
    public static final String BI_Default_As = " as ";

    public static final String CONNECTION_TABLE = "TABLE";
    public static final String CONNECTION_VIEW = "VIEW";
    public static final String CONNECTION_TABLE_NAME = "TABLE_NAME";
    public static final String CONNECTION_Field_Template = "Select top 1 * from {0}";

    public static final String AGG_SameAs_Template = "({0}) as {0}";
    public static final String AGG_DifferentAs_Template = "{0} as {1}";
    public static final String AGG_Sum_Template = "sum({0}) as {1}";
    public static final String AGG_SameSum_Template = "sum({0}) as {0}";
    public static final String Agg_Target = "target";
    public static final String Agg_Source= "source";
    public static final String LeftJoinFactor_Template = "{0}.{1} = {2}.{3}";
    public static final String LeftJointableAs = "{0} {1}";
    public static final String LeftJoinMain_Template = "LEFT JOIN maintable on leftjionfactor";
    public static final String LeftJoin_Template = "LEFT JOIN {0} {1} on {1}.{2} = {3}.{4}";
    public static final String LeftJoin_MainTable = "maintable";
    public static final String LeftJoin_Leftjionfactor = "leftjionfactor";
    public static final String Select_Field_Template = "{0}.{1}";


    public static final String AGG_GroupInfo_Template = "(总聚合数：{0}=====当前聚合主题组：{1}";
    public static final String AGG_Info_Template = "总聚合数：{0}==当前聚合数{1}===当前聚合主题组：{2}===当前聚合主题{3}";
    public static final String AGG_NullDimension_Template = "当前表：{0}=====维度：{1} 为空";
    public static final String AGG_NullField_Template = "null as ";
    public static final String AGG_IS_NULL = " is  null";
    public static final String AGG_IS_NOT_NULL = " is not null";
    public static final String AGG_IS_NOT_Empty = "  <> ''";
    public static final String MDF = "mdf";

    public static ArrayList<String> filedUnCatchList = new ArrayList<>();
    public static ArrayList<String> DDTTableList = new ArrayList<>();

    public static HashMap<String, String> AchieveMustTableNameMap = new HashMap<>();
    public static ArrayList<String> AchieveCanTableNameList = new ArrayList<>();

    public static HashMap<Integer, AggDirection> AggDefaultSqlTypeMap = new HashMap<>();

    public static HashMap<EAchieveDataType, String> DataTypeMap = new HashMap<>();


    public static String peroid = "peroid";
    public static String DDI_S = "DDIDaily_S";
    //public static String DDI_P = "DDIDaily_P";
    public static String DDI_P = "DataP";
    public static String DDI_I = "DDIDaily_I";

    public static String month = "month";
    public static String agg = "agg";
    public static String roxolidachieve = "roxolidachieve";
    public static String achieve = "achieve";
    public static String mGrowth = "mgrowth";
    public static String yGrowth = "ygrowth";
    public static String roxolidyGrowth = "roxolidygrowth";
    public static String roxolidtarget = "roxolidtarget";
    public static String target = "target";
    public static String Raw = "raw";
    public static String RawIn = "rawin";
    public static String Real = "real";
    public static String RealOut = "realout";
    public static String fields = "fields";
    public static String filter = "filter";
    public static String Total = "total";
    public static String Roxolid = "Roxolid";
    public static String lastDataTimeSql = "getLastDataTime";
    public static String lastDataTimeByPersonSql = "getLastDataTimeByPerson";
    public static String DDITable = "DDITable";
    public static String BizDate = "BizDate";
    public static String dataName = "dataName";
    public static String dataType = "dataType";
    public static String brandType = "brandType";
    public static String organizationJoin = "organizationJoin";
    public static String organization = "organization";
    public static String LoginName = "LoginName";
    public static String UserID = "UserID";
    public static String ParentID = "ParentID";
    public static String RSMRegion = "RSMRegion";
    public static String Region = "region";
    public static String DistributorHierarchy = "DistributorHierarchy";
    public static String DistributorCode = "DistributorCode";
    public static String distributor = "distributor";
    public static String Achieve_TableName_Template = "VIEW_{0}_Month_{1}";
    public static String RSM = "RSM";
    public static String datatypecode = "datatypecode";



    public static String Peroid_Year = "year";
    public static String Peroid_Quarter = "quarter";
    public static String Peroid_Month = "month";
    public static String monthno = "monthno";
    public static String PageFilter = "pageFilter";
    public static String A01 = "a01";
    public static String userType = "userType";
    public static String SuperAdmin = "superAdmin";
    public static String GroupBy = "GroupBy";
    public static String Brand = "brand";
    public static String userId = "userId";
    public static String Territory = "territory";
    public static String Name = "name";
    public static String Product = "Product";
    public static String ProductCode = "ProductCode";
    public static String partitionName = "partitionName";
    public static String DefaultAggPartitionName = "ps_aggCode(aggcode)";
    public static String SqlServer_TextSql = "SELECT 'x'";





    public static String Create_FILEGROUP_Template = "ALTER DATABASE {0} ADD FILEGROUP {1}";


    public static String Create_PartitionFunc_Template = "CREATE PARTITION FUNCTION pf_{0}_aggcode(nvarchar(254)) " +
            "AS RANGE right " +
            "FOR VALUES ({1})";

    public static String Create_PartitionScheme_Template = "create partition scheme ps_{0}_aggcode " +
            "as partition pf_{0}_aggcode " +
            "to({1})";

    public static String Create_FILE_Template = "ALTER DATABASE {0} " +
            "ADD FILE(  " +
            "NAME=N{1},FILENAME={2},SIZE=3MB " +
            ",FILEGROWTH=5MB " +
            ") TO FILEGROUP {3}";



    static {
        filedUnCatchList.add(BI_Field_Id);
        filedUnCatchList.add(BI_Field_Active);
        filedUnCatchList.add(BI_Field_No);
        filedUnCatchList.add(BI_Field_Topictype);
        filedUnCatchList.add(BI_Field_Aggcode);

        filedUnCatchList.add(BI_Field_Flowid);

        AggDefaultSqlTypeMap.put(Types.INTEGER, AggDirection.Dimension);
        AggDefaultSqlTypeMap.put(Types.VARCHAR, AggDirection.Dimension);
        AggDefaultSqlTypeMap.put(Types.NVARCHAR, AggDirection.Dimension);

        AggDefaultSqlTypeMap.put(Types.DECIMAL, AggDirection.Measurment);

        DDTTableList.add(DDI_S);
        DDTTableList.add(DDI_P);
        DDTTableList.add(DDI_I);

        AchieveCanTableNameList.add("o" );
        AchieveCanTableNameList.add("d" );

        AchieveMustTableNameMap.put("pr", "brand");
        AchieveMustTableNameMap.put("pe", "peroid");



        DataTypeMap.put(EAchieveDataType.sellin, "Supervisor");
        DataTypeMap.put(EAchieveDataType.sellout, "Salesperson");
        DataTypeMap.put(EAchieveDataType.Distributor, "distributor");
        DataTypeMap.put(EAchieveDataType.Sale, EAchieveDataType.Sale.name());
        DataTypeMap.put(EAchieveDataType.Inventory, EAchieveDataType.Inventory.name());
        DataTypeMap.put(EAchieveDataType.Purchase, EAchieveDataType.Purchase.name());
    }

    public static ArrayList<String> getDDITableds() {
        return DDTTableList;
    }


}
