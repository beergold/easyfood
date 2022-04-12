package com.wjj.worker.framework.request;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author BeerGod
 * 自定义参数获取类
 */
public class WjjApiParameter extends HashMap<String, Object>{

    private static final long serialVersionUID = 8314496209039423607L;

    public WjjApiParameter putAllNotEmpty(Map<String, Object> value) {
        Iterator var2 = value.entrySet().iterator();

        while (var2.hasNext()) {
            Entry<String, Object> map = (Entry) var2.next();
            if (ObjectUtil.isNotEmpty(map.getValue())) {
                this.put(map.getKey(), map.getValue());
            }
        }

        return this;
    }

    public WjjApiParameter clearput(String key, Object value) {
        this.clear();
        super.put(key, value);
        return this;
    }

    public List<WjjApiParameter> getList(String key) {
        return (List) (this.containsKey(key) && this.get(key) != null ? (List) this.get(key) : new ArrayList());
    }

    public List<WjjApiParameter> getListWithJson(String key) {
        if (this.containsKey(key)) {
            String json = this.getString(key, "");
            if (StrUtil.isNotEmpty(json)) {
                try {
                    return JSONArray.parseArray(json, WjjApiParameter.class);
                } catch (RuntimeException var4) {
                    var4.printStackTrace();
                }
            }
        }

        return new ArrayList();
    }

    public WjjApiParameter getJsonObject(String key) {
        if (this.containsKey(key)) {
            String json = this.getString(key, "{}");

            try {
                return JSONObject.parseObject(json, WjjApiParameter.class);
            } catch (RuntimeException var4) {
                var4.printStackTrace();
            }
        }

        return new WjjApiParameter();
    }


    public byte[] getBytes(String key) {
        if (!this.containsKey(key)) {
            return null;
        } else {
            Object obj = this.get(key);
            if (obj instanceof byte[]) {
                return (byte[]) ((byte[]) obj);
            } else if (obj instanceof String) {
                String base64 = (String) obj;
                String[] fileArray = base64.split(",");
                return fileArray.length == 2 ? Base64.getDecoder().decode(fileArray[1]) : Base64.getDecoder().decode(base64);
            } else {
                return null;
            }
        }
    }


    public Object put(Object value) {
        return super.put(value.getClass().getName(), value);
    }

    public <T> T get(String key, Class<T> clazz) {
        if (this.containsKey(key)) {
            Object obj = this.get(key);
            if (obj.getClass() == clazz) {
                return (T) obj;
            } else {
                return obj.getClass() == String.class ? JSONObject.parseObject(obj.toString(), clazz) : null;
            }
        } else {
            return null;
        }
    }

    public <T> T get(Class<T> clazz) {
        String key = clazz.getName();
        return this.get(key, clazz);
    }

    public String getString(String key) {
        return this.containsKey(key) && this.get(key) != null ? this.get(key).toString() : null;
    }

    public String getString(String key, String def) {
        String value = this.getString(key);
        return StrUtil.isEmpty(value) ? def : value;
    }

    public Boolean getBoolean(String key) {
        String value = this.getString(key);
        if (StrUtil.isEmpty(value)) {
            return null;
        } else {
            return !"false".equalsIgnoreCase(value) && !"0".equals(value) ? true : false;
        }
    }

    public Boolean getBoolean(String key, boolean def) {
        Boolean value = this.getBoolean(key);
        return value == null ? def : value;
    }

    public Character getCharacter(String key) {
        String value = this.getString(key);
        return StrUtil.isEmpty(value) ? null : value.charAt(0);
    }


    public Integer getInteger(String key) {
        String value = this.getString(key);
        return StringUtils.isNumeric(value) ? Integer.valueOf(value) : null;
    }

    public Integer getInteger(String key, int def) {
        Integer value = this.getInteger(key);
        return value == null ? def : value;
    }

    public Long getLong(String key) {
        String value = this.getString(key);
        return StringUtils.isNumeric(value) ? Long.valueOf(value) : null;
    }

    public Long getLong(String key, Long def) {
        Long value = this.getLong(key);
        return value == null ? def : value;
    }

    public Float getFloat(String key) {
        String value = this.getString(key);
        return StringUtils.isNumeric(value) ? Float.valueOf(value) : null;
    }

    public Double getDouble(String key) {
        String value = this.getString(key);

        try {
            return Double.valueOf(value);
        } catch (Exception var4) {
            return null;
        }
    }

    public Double getDouble(String key, Double def) {
        Double value = this.getDouble(key);
        return value == null ? def : value;
    }

    public Date getDate(String key) {
        return this.getDate(key, "yyyy/MM/dd");
    }

    public Time getTime(String key) {
        String str = this.getString(key);

        try {
            String[] times = str.split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(11, Integer.valueOf(times[0]));
            calendar.set(12, Integer.valueOf(times[1]));
            if (times.length == 3) {
                calendar.set(13, Integer.valueOf(times[2]));
            } else {
                calendar.set(13, 0);
            }

            return new Time(calendar.getTimeInMillis());
        } catch (Exception var5) {
            return null;
        }
    }

    public java.sql.Date getSqlDate(String key) {
        return this.getSqlDate(key, "yyyy/MM/dd");
    }

    public java.sql.Date getSqlDate(String key, String pattern) {
        Date date = this.getDate(key, pattern);
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }

    public Timestamp getTimestamp(String key) {
        Object obj = this.get(key);
        if (obj == null) {
            return null;
        } else if (obj instanceof Timestamp) {
            return (Timestamp) obj;
        } else {
            String value = obj.toString();
            if (StringUtils.isNumeric(value)) {
                try {
                    return new Timestamp(Long.valueOf(value));
                } catch (Exception var6) {
                    return null;
                }
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

                try {
                    Date date = format.parse(value);
                    return new Timestamp(date.getTime());
                } catch (ParseException var7) {
                    return null;
                }
            }
        }
    }

    public Date getDate(String key, String pattern) {
        Date date = null;
        Object obj = this.get(key);
        if (obj == null) {
            return date;
        } else if (obj instanceof Date) {
            return (Date) obj;
        } else {
            String value = this.getString(key);
            if (!StringUtils.isEmpty(value)) {
                SimpleDateFormat format = new SimpleDateFormat(pattern);

                try {
                    date = format.parse(value);
                } catch (ParseException var8) {
                }
            }

            return date;
        }
    }

    public String[] getArray(String key, String regex) {
        String value = this.getString(key);
        return StrUtil.isEmpty(value) ? new String[0] : value.split(regex);
    }

    @Override
    public String toString() {
        return this.toJSON();
    }

    public String toJSON() {
        return JSONObject.toJSONString(this);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        if (this.containsKey(key)) {
            Object value = this.get(key);
            if (value instanceof List) {
                return (List)value;
            } else {
                String data = this.getString(key);
                return (List)(StringUtils.isEmpty(data) ? new ArrayList() : JSONArray.parseArray(data, clazz));
            }
        } else {
            String json = this.getString(key);
            return (List)(StringUtils.isNotEmpty(json) ? JSONArray.parseArray(json, clazz) : new ArrayList());
        }
    }
}
