package bi;

import bi.define.AggOperator;
import bi.define.EAggCreateCode;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.persist.DataHandler;
import foundation.persist.SqlSession;
import foundation.persist.sql.NamedSQL;
import foundation.persist.sql.SQLRunner;
import foundation.util.ContentBuilder;
import foundation.util.Util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author kimi
 * @description 聚合工具类
 * @date 2019-01-15 15:23
 */


public class AggUtil {
    public static final String initSeparator = "-";
    public static boolean checkTableExists(String tableName) {
        Connection connection = SqlSession.createConnection();
        ResultSet rs = null;
        try {
            rs = connection.getMetaData().getTables(null, null, tableName, null);
            if (rs.next()) {
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static EAggCreateCode createAggTable(String aggTableName, String groupId, List<String> oneDimensionGroups, List<String> measurmentList, String topicType) throws Exception {
        AggOperator aggOperator = AggOperator.valueOf(groupId);
        if (aggOperator == null) {
            return EAggCreateCode.uncreated;
        }

        if (!(oneDimensionGroups.contains(AggConstant.peroid))) {
            //剔除不含期间维度的表
            return EAggCreateCode.parse;
        }

        if (!(oneDimensionGroups.contains(AggConstant.organization) || oneDimensionGroups.contains(AggConstant.distributor))) {
            return EAggCreateCode.parse;
        }

        ArrayList<String> dimensionCodeList = new ArrayList<>();
        for (String oneDimensionGroup : oneDimensionGroups) {
            EntitySet dataSet = DataHandler.getDataSet(AggConstant.BI_TABLE_Dimension, AggConstant.BI_Filter_Active + Util.And + Util.quotedEqualStr(AggConstant.BI_Field_GroupId, oneDimensionGroup));
            for (Entity entity : dataSet) {
                String code = entity.getString(AggConstant.BI_Field_Code);

                dimensionCodeList.add(code);
            }
        }
        String fields = dimensionCodeList.stream()
                .map(dimension -> combineDimensionField(dimension))
                .collect(Collectors.joining(Util.comma));


        String measurments = measurmentList.stream()
                .map(measurment -> MessageFormat.format(AggConstant.Create_AggField_Template, measurment, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength))
                .collect(Collectors.joining(Util.comma));
        fields += Util.stringJoin(Util.comma, measurments);

       /* String target = MessageFormat.format(AggConstant.Create_AggField_Template, AggConstant.target, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength);
        fields += Util.stringJoin(Util.comma, target);

        String achieve = MessageFormat.format(AggConstant.Create_AggField_Template, AggConstant.achieve, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength);
        fields += Util.stringJoin(Util.comma, achieve);

        String yGrowth = MessageFormat.format(AggConstant.Create_AggField_Template, AggConstant.yGrowth, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength);
        fields += Util.stringJoin(Util.comma, yGrowth);

        String roxolidTarget = MessageFormat.format(AggConstant.Create_AggField_Template, AggConstant.roxolidtarget, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength);
        fields += Util.stringJoin(Util.comma, roxolidTarget);

        String roxolidachieve = MessageFormat.format(AggConstant.Create_AggField_Template, AggConstant.roxolidachieve, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength);
        fields += Util.stringJoin(Util.comma, roxolidachieve);

        String roxolidyGrowth = MessageFormat.format(AggConstant.Create_AggField_Template, AggConstant.roxolidyGrowth, AggConstant.AggField_Default_Length, AggConstant.AggField_Default_decimalLength);
        fields += Util.stringJoin(Util.comma, roxolidyGrowth);
*/

        NamedSQL createSql = NamedSQL.getInstance(AggConstant.Sql_createAggPartitionTableTemplate);
        createSql.setParam(AggConstant.Sql_Field_tableName, aggTableName);
        createSql.setParam(AggConstant.Sql_Field_fields, fields);
        createSql.setParam(AggConstant.partitionName, MessageFormat.format("ps_{0}_aggcode(aggcode)", topicType));
        int i = SQLRunner.execSQL(createSql);
        if (i == 0) {
            return EAggCreateCode.created;
        } else {
            return EAggCreateCode.uncreated;
        }
    }

    private static String combineDimensionField(String dimension) {
        String format;
        if ("bizdate".equalsIgnoreCase(dimension)) {
            format = MessageFormat.format(AggConstant.Create_DateField_Template, dimension);

        } else if("year".equalsIgnoreCase(dimension) || "quarter".equalsIgnoreCase(dimension) || "month".equalsIgnoreCase(dimension) || "monthno".equalsIgnoreCase(dimension)){
            format = MessageFormat.format(AggConstant.Create_IntField_Template, dimension);

        }else {
            format = MessageFormat.format(AggConstant.Create_CommonField_Template, dimension, AggConstant.CommonField_Default_Length);
        }

        return format;
    }


    public static String[] getSortString(String[] keys){

         for (int i = 0; i < keys.length - 1; i++) {
             for (int j = 0; j < keys.length - i -1; j++) {
                 String pre = keys[j];
                 String next = keys[j + 1];
                 if(isMoreThan(pre, next)){
                     String temp = pre;
                     keys[j] = next;
                     keys[j+1] = temp;
                 }
             }
         }
         return keys;
    }
    private static boolean isMoreThan(String pre, String next){
         if(null == pre || null == next || "".equals(pre) || "".equals(next)){
             return false;
         }

         char[] c_pre = pre.toCharArray();
         char[] c_next = next.toCharArray();

         int minSize = Math.min(c_pre.length, c_next.length);

         for (int i = 0; i < minSize; i++) {
             if((int)c_pre[i] > (int)c_next[i]){
                 return true;
             }else if((int)c_pre[i] < (int)c_next[i]){
                 return false;
             }
         }
         if(c_pre.length > c_next.length){
             return true;
         }

         return false;
     }

    public static  String calculateTableName(ArrayList<String> dimensionList, ArrayList<String> maxSizeDimensionList) {
        ContentBuilder builder = new ContentBuilder(Util.SubSeparator);

        Collections.sort(dimensionList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        for (String dimension : dimensionList) {
            String subDimension = getNoSameSub(dimension, maxSizeDimensionList);
            builder.append(subDimension);
        }

        return builder.toString();
    }

    public static String getNoSameSub(String dimension, ArrayList<String> dimensionList) {
        int index = 1;

        while (index < dimension.length()) {
            dimension = dimension.toLowerCase();
            String subDimension = dimension.substring(0, index);
            boolean sameSub = true;
            for (String oneDimension : dimensionList) {
                oneDimension = oneDimension.toLowerCase();
                if (oneDimension.equalsIgnoreCase(dimension)) {
                    continue;
                }
                sameSub = oneDimension.startsWith(subDimension);
                if (sameSub) {
                    break;
                }
            }
            if (!sameSub) {
                return subDimension;
            }

            index++;
        }
        return dimension;
    }

    public static String getSubPosition(String loginName) {
        try {
            NamedSQL getSubPositionById = NamedSQL.getInstance("getSubPositionById");
            getSubPositionById.setParam("id", loginName);
            Entity entity = SQLRunner.getEntity(getSubPositionById);
            String type = entity.getString("type");
            return type;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
