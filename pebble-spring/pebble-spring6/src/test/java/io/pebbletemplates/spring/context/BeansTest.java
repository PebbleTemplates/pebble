package io.pebbletemplates.spring.context;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeansTest {

  @Test
  void readsBeansAndDefinitionNamesFromApplicationContext() {
    StaticApplicationContext context = new StaticApplicationContext();
    context.getDefaultListableBeanFactory().registerBeanDefinition("sampleBean",
        new RootBeanDefinition(Object.class));
    context.refresh();

    Object bean = context.getBean("sampleBean");
    Beans beans = new Beans(context);
    assertTrue(beans.containsKey("sampleBean"));
    assertTrue(beans.containsKey(new Object() {
      @Override
      public String toString() {
        return "sampleBean";
      }
    }));
    assertFalse(beans.containsKey("missingBean"));
    assertSame(bean, beans.get("sampleBean"));
    assertEquals(Collections.singleton("sampleBean"), beans.keySet());
    assertEquals(1, beans.size());
    assertFalse(beans.isEmpty());
    context.close();
  }

  @Test
  void emptyContextAndReadOnlyMapContract() {
    StaticApplicationContext context = new StaticApplicationContext();
    context.refresh();
    Beans beans = new Beans(context);

    assertTrue(beans.isEmpty());
    assertEquals(0, beans.size());
    assertThrows(IllegalArgumentException.class, () -> beans.get(null));
    assertThrows(IllegalArgumentException.class, () -> beans.containsKey(null));
    assertThrows(UnsupportedOperationException.class, beans::clear);
    assertThrows(UnsupportedOperationException.class, () -> beans.containsValue("value"));
    assertThrows(UnsupportedOperationException.class, beans::entrySet);
    assertThrows(UnsupportedOperationException.class, () -> beans.put("key", "value"));
    assertThrows(UnsupportedOperationException.class, () -> beans.putAll(Collections.emptyMap()));
    assertThrows(UnsupportedOperationException.class, () -> beans.remove("key"));
    assertThrows(UnsupportedOperationException.class, beans::values);
    context.close();
  }
}
