package vn.fastpos.admin.dto.Admin;

import jakarta.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

public final class ViewMapper {
  private ViewMapper() {}

  public static Map<String, Object> of(Object entity) {
    entity = org.hibernate.Hibernate.unproxy(entity);
    var out = new LinkedHashMap<String, Object>();
    // Only scalar columns are exposed; lazy relations and credential hash stay private.
    for (Field f : entity.getClass().getDeclaredFields()) {
      var column = f.getAnnotation(Column.class);
      if (f.getName().equals("matKhauHash")) continue;
      try {
        f.setAccessible(true);
        if (column != null) out.put(column.name(), f.get(entity));
        if (f.isAnnotationPresent(EmbeddedId.class) && f.get(entity) != null)
          out.putAll(of(f.get(entity)));
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
    }
    return out;
  }
}
