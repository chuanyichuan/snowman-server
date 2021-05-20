package cc.kevinlu.snow.server.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DigitDOExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DigitDOExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdIsNull() {
            addCriterion("service_instance_id is null");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdIsNotNull() {
            addCriterion("service_instance_id is not null");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdEqualTo(Long value) {
            addCriterion("service_instance_id =", value, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdNotEqualTo(Long value) {
            addCriterion("service_instance_id <>", value, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdGreaterThan(Long value) {
            addCriterion("service_instance_id >", value, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdGreaterThanOrEqualTo(Long value) {
            addCriterion("service_instance_id >=", value, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdLessThan(Long value) {
            addCriterion("service_instance_id <", value, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdLessThanOrEqualTo(Long value) {
            addCriterion("service_instance_id <=", value, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdIn(List<Long> values) {
            addCriterion("service_instance_id in", values, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdNotIn(List<Long> values) {
            addCriterion("service_instance_id not in", values, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdBetween(Long value1, Long value2) {
            addCriterion("service_instance_id between", value1, value2, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andServiceInstanceIdNotBetween(Long value1, Long value2) {
            addCriterion("service_instance_id not between", value1, value2, "serviceInstanceId");
            return (Criteria) this;
        }

        public Criteria andChunkIsNull() {
            addCriterion("chunk is null");
            return (Criteria) this;
        }

        public Criteria andChunkIsNotNull() {
            addCriterion("chunk is not null");
            return (Criteria) this;
        }

        public Criteria andChunkEqualTo(Integer value) {
            addCriterion("chunk =", value, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkNotEqualTo(Integer value) {
            addCriterion("chunk <>", value, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkGreaterThan(Integer value) {
            addCriterion("chunk >", value, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkGreaterThanOrEqualTo(Integer value) {
            addCriterion("chunk >=", value, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkLessThan(Integer value) {
            addCriterion("chunk <", value, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkLessThanOrEqualTo(Integer value) {
            addCriterion("chunk <=", value, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkIn(List<Integer> values) {
            addCriterion("chunk in", values, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkNotIn(List<Integer> values) {
            addCriterion("chunk not in", values, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkBetween(Integer value1, Integer value2) {
            addCriterion("chunk between", value1, value2, "chunk");
            return (Criteria) this;
        }

        public Criteria andChunkNotBetween(Integer value1, Integer value2) {
            addCriterion("chunk not between", value1, value2, "chunk");
            return (Criteria) this;
        }

        public Criteria andFromValueIsNull() {
            addCriterion("from_value is null");
            return (Criteria) this;
        }

        public Criteria andFromValueIsNotNull() {
            addCriterion("from_value is not null");
            return (Criteria) this;
        }

        public Criteria andFromValueEqualTo(Long value) {
            addCriterion("from_value =", value, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueNotEqualTo(Long value) {
            addCriterion("from_value <>", value, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueGreaterThan(Long value) {
            addCriterion("from_value >", value, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueGreaterThanOrEqualTo(Long value) {
            addCriterion("from_value >=", value, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueLessThan(Long value) {
            addCriterion("from_value <", value, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueLessThanOrEqualTo(Long value) {
            addCriterion("from_value <=", value, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueIn(List<Long> values) {
            addCriterion("from_value in", values, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueNotIn(List<Long> values) {
            addCriterion("from_value not in", values, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueBetween(Long value1, Long value2) {
            addCriterion("from_value between", value1, value2, "fromValue");
            return (Criteria) this;
        }

        public Criteria andFromValueNotBetween(Long value1, Long value2) {
            addCriterion("from_value not between", value1, value2, "fromValue");
            return (Criteria) this;
        }

        public Criteria andToValueIsNull() {
            addCriterion("to_value is null");
            return (Criteria) this;
        }

        public Criteria andToValueIsNotNull() {
            addCriterion("to_value is not null");
            return (Criteria) this;
        }

        public Criteria andToValueEqualTo(Long value) {
            addCriterion("to_value =", value, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueNotEqualTo(Long value) {
            addCriterion("to_value <>", value, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueGreaterThan(Long value) {
            addCriterion("to_value >", value, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueGreaterThanOrEqualTo(Long value) {
            addCriterion("to_value >=", value, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueLessThan(Long value) {
            addCriterion("to_value <", value, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueLessThanOrEqualTo(Long value) {
            addCriterion("to_value <=", value, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueIn(List<Long> values) {
            addCriterion("to_value in", values, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueNotIn(List<Long> values) {
            addCriterion("to_value not in", values, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueBetween(Long value1, Long value2) {
            addCriterion("to_value between", value1, value2, "toValue");
            return (Criteria) this;
        }

        public Criteria andToValueNotBetween(Long value1, Long value2) {
            addCriterion("to_value not between", value1, value2, "toValue");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedIsNull() {
            addCriterion("gmt_created is null");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedIsNotNull() {
            addCriterion("gmt_created is not null");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedEqualTo(Date value) {
            addCriterion("gmt_created =", value, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedNotEqualTo(Date value) {
            addCriterion("gmt_created <>", value, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedGreaterThan(Date value) {
            addCriterion("gmt_created >", value, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedGreaterThanOrEqualTo(Date value) {
            addCriterion("gmt_created >=", value, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedLessThan(Date value) {
            addCriterion("gmt_created <", value, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedLessThanOrEqualTo(Date value) {
            addCriterion("gmt_created <=", value, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedIn(List<Date> values) {
            addCriterion("gmt_created in", values, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedNotIn(List<Date> values) {
            addCriterion("gmt_created not in", values, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedBetween(Date value1, Date value2) {
            addCriterion("gmt_created between", value1, value2, "gmtCreated");
            return (Criteria) this;
        }

        public Criteria andGmtCreatedNotBetween(Date value1, Date value2) {
            addCriterion("gmt_created not between", value1, value2, "gmtCreated");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}