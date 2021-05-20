/**
 * MIT License
 *
 * Copyright (c) 2021 chuanyichuan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cc.kevinlu.snow.server.config.mybatis;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * @author cc
 */
public class JavaTypeResolverImpl implements JavaTypeResolver {
    protected List<String>                                                  warnings;
    protected Properties                                                    properties = new Properties();
    protected Context                                                       context;
    protected boolean                                                       forceBigDecimals;
    protected Map<Integer, JavaTypeResolverDefaultImpl.JdbcTypeInformation> typeMap    = new HashMap();

    public JavaTypeResolverImpl() {
        typeMap.put(Types.ARRAY, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("ARRAY",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.BIGINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BIGINT",
                new FullyQualifiedJavaType(Long.class.getName())));
        typeMap.put(Types.BINARY,
                new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BINARY", new FullyQualifiedJavaType("byte[]")));
        typeMap.put(Types.BIT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BIT",
                new FullyQualifiedJavaType(Boolean.class.getName())));
        typeMap.put(Types.BLOB,
                new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BLOB", new FullyQualifiedJavaType("byte[]")));
        typeMap.put(Types.BOOLEAN, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("BOOLEAN",
                new FullyQualifiedJavaType(Boolean.class.getName())));
        typeMap.put(Types.CHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("CHAR",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.CLOB, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("CLOB",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.DATALINK, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DATALINK",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.DATE, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DATE",
                new FullyQualifiedJavaType(Date.class.getName())));
        typeMap.put(Types.DECIMAL, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DECIMAL",
                new FullyQualifiedJavaType(BigDecimal.class.getName())));
        typeMap.put(Types.DISTINCT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DISTINCT",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.DOUBLE, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("DOUBLE",
                new FullyQualifiedJavaType(Double.class.getName())));
        typeMap.put(Types.FLOAT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("FLOAT",
                new FullyQualifiedJavaType(Double.class.getName())));
        typeMap.put(Types.INTEGER, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("INTEGER",
                new FullyQualifiedJavaType(Integer.class.getName())));
        typeMap.put(Types.JAVA_OBJECT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("JAVA_OBJECT",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.LONGNVARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGNVARCHAR",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.LONGVARBINARY, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGVARBINARY",
                new FullyQualifiedJavaType("byte[]")));
        typeMap.put(Types.LONGVARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("LONGVARCHAR",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NCHAR",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NCLOB, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NCLOB",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NVARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NVARCHAR",
                new FullyQualifiedJavaType(String.class.getName())));
        typeMap.put(Types.NULL, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NULL",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.NUMERIC, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("NUMERIC",
                new FullyQualifiedJavaType(BigDecimal.class.getName())));
        typeMap.put(Types.OTHER, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("OTHER",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.REAL, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("REAL",
                new FullyQualifiedJavaType(Float.class.getName())));
        typeMap.put(Types.REF, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("REF",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.SMALLINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("SMALLINT",
                new FullyQualifiedJavaType(Integer.class.getName())));
        typeMap.put(Types.STRUCT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("STRUCT",
                new FullyQualifiedJavaType(Object.class.getName())));
        typeMap.put(Types.TIME, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIME",
                new FullyQualifiedJavaType(Date.class.getName())));
        typeMap.put(Types.TIMESTAMP, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TIMESTAMP",
                new FullyQualifiedJavaType(Date.class.getName())));
        typeMap.put(Types.TINYINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TINYINT",
                new FullyQualifiedJavaType(Integer.class.getName())));
        typeMap.put(Types.VARBINARY,
                new JavaTypeResolverDefaultImpl.JdbcTypeInformation("VARBINARY", new FullyQualifiedJavaType("byte[]")));
        typeMap.put(Types.VARCHAR, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("VARCHAR",
                new FullyQualifiedJavaType(String.class.getName())));
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        this.forceBigDecimals = StringUtility.isTrue(properties.getProperty("forceBigDecimals"));
    }

    @Override
    public FullyQualifiedJavaType calculateJavaType(IntrospectedColumn introspectedColumn) {
        FullyQualifiedJavaType answer = null;
        JavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = this.typeMap
                .get(introspectedColumn.getJdbcType());
        if (jdbcTypeInformation != null) {
            answer = jdbcTypeInformation.getFullyQualifiedJavaType();
            answer = this.overrideDefaultType(introspectedColumn, answer);
        }

        return answer;
    }

    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column,
                                                         FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;
        switch (column.getJdbcType()) {
            case -7:
                answer = this.calculateBitReplacement(column, defaultType);
                break;
            case 2:
            case 3:
                answer = this.calculateBigDecimalReplacement(column, defaultType);
        }

        return answer;
    }

    protected FullyQualifiedJavaType calculateBitReplacement(IntrospectedColumn column,
                                                             FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer;
        if (column.getLength() > 1) {
            answer = new FullyQualifiedJavaType("byte[]");
        } else {
            answer = defaultType;
        }

        return answer;
    }

    protected FullyQualifiedJavaType calculateBigDecimalReplacement(IntrospectedColumn column,
                                                                    FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer;
        if (column.getScale() <= 0 && column.getLength() <= 18 && !this.forceBigDecimals) {
            if (column.getLength() > 9) {
                answer = new FullyQualifiedJavaType(Double.class.getName());
            } else if (column.getLength() > 4) {
                answer = new FullyQualifiedJavaType(Integer.class.getName());
            } else {
                answer = new FullyQualifiedJavaType(Short.class.getName());
            }
        } else {
            answer = defaultType;
        }

        return answer;
    }

    @Override
    public String calculateJdbcTypeName(IntrospectedColumn introspectedColumn) {
        String answer = null;
        JavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = this.typeMap
                .get(introspectedColumn.getJdbcType());
        if (jdbcTypeInformation != null) {
            answer = jdbcTypeInformation.getJdbcTypeName();
        }

        return answer;
    }

    @Override
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    public static class JdbcTypeInformation {
        private final String                 jdbcTypeName;
        private final FullyQualifiedJavaType fullyQualifiedJavaType;

        public JdbcTypeInformation(String jdbcTypeName, FullyQualifiedJavaType fullyQualifiedJavaType) {
            this.jdbcTypeName = jdbcTypeName;
            this.fullyQualifiedJavaType = fullyQualifiedJavaType;
        }

        public String getJdbcTypeName() {
            return this.jdbcTypeName;
        }

        public FullyQualifiedJavaType getFullyQualifiedJavaType() {
            return this.fullyQualifiedJavaType;
        }
    }

}
