/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.cdp.workbench;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class AiWorkbenchServiceTest {
  private final AiWorkbenchService service = new AiWorkbenchService();

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  private Map<String, Object> sample() {
    return Map.<String, Object>ofEntries(
        Map.entry("asOf", "2026-10-23T11:00:00Z"),
        Map.entry("purpose", "NEWSLETTER"),
        Map.entry("channel", "EMAIL"),
        Map.entry("subjects", List.of("U1", "U2")),
        Map.entry(
            "events",
            List.of(
                Map.<String, Object>ofEntries(
                    Map.entry("id", "E1"),
                    Map.entry("subject", "U1"),
                    Map.entry("purpose", "NEWSLETTER"),
                    Map.entry("channel", "EMAIL"),
                    Map.entry("status", "GRANTED"),
                    Map.entry("at", "2026-10-23T09:00:00Z"),
                    Map.entry("expiresAt", "")),
                Map.<String, Object>ofEntries(
                    Map.entry("id", "E2"),
                    Map.entry("subject", "U1"),
                    Map.entry("purpose", "NEWSLETTER"),
                    Map.entry("channel", "EMAIL"),
                    Map.entry("status", "REVOKED"),
                    Map.entry("at", "2026-10-23T10:00:00Z"),
                    Map.entry("expiresAt", "")))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void computesBusinessResult() {
    var result = service.execute(sample());
    assertEquals("REVOKED", ((Map<?, ?>) ((List<?>) result.get("subjects")).get(0)).get("state"));
    assertEquals("NO_RECORD", ((Map<?, ?>) ((List<?>) result.get("subjects")).get(1)).get("state"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void coversFeatureBoundary() {
    var invalid = new HashMap<>(sample());
    invalid.put("asOf", "bad");
    assertThrows(IllegalArgumentException.class, () -> service.execute(invalid));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void coversAdditionalBoundaryOne() {
    var r = new HashMap<>(sample());
    r.put("asOf", "2026-10-23T09:30:00Z");
    assertEquals(
        "GRANTED",
        ((Map<?, ?>) ((List<?>) service.execute(r).get("subjects")).get(0)).get("state"));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void coversAdditionalBoundaryTwo() {
    var r = new HashMap<>(sample());
    var events = (List<?>) r.get("events");
    var duplicate = new HashMap<>((Map<String, Object>) events.get(0));
    duplicate.put("id", "E3");
    r.put("events", List.of(events.get(0), duplicate));
    assertThrows(IllegalArgumentException.class, () -> service.execute(r));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsExcessiveNumericScale() {
    assertThrows(
        IllegalArgumentException.class,
        () -> WorkbenchInput.decimal(Map.of("v", new BigDecimal("1e-1000000")), "v", 0, 1));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            WorkbenchInput.decimal(
                Map.of("v", new BigDecimal("0.1234567890123456789012345678901")), "v", 0, 1));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void deterministicAndDoesNotMutateInput() {
    var input = sample();
    String before = input.toString();
    assertEquals(service.execute(input), service.execute(input));
    assertEquals(before, input.toString());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsNull() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(null));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsMissingFields() {
    assertThrows(IllegalArgumentException.class, () -> service.execute(Map.of()));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsUnknownFields() {
    var input = new HashMap<>(sample());
    input.put("__unknown", true);
    assertThrows(IllegalArgumentException.class, () -> service.execute(input));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsOversizedText() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.execute(Map.of("payload", "x".repeat(64001))));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsDeepInput() {
    Map<String, Object> v = Map.of("x", 1);
    for (int i = 0; i < 14; i++) v = Map.of("x", v);
    final var input = v;
    assertThrows(IllegalArgumentException.class, () -> service.execute(input));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void httpRequiresAuthentication() throws Exception {
    var mvc = MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();
    mvc.perform(
            post("/api/workbench/consent-event-snapshot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"asOf\":\"2026-10-23T11:00:00Z\",\"purpose\":\"NEWSLETTER\",\"channel\":\"EMAIL\",\"subjects\":[\"U1\",\"U2\"],\"events\":[{\"id\":\"E1\",\"subject\":\"U1\",\"purpose\":\"NEWSLETTER\",\"channel\":\"EMAIL\",\"status\":\"GRANTED\",\"at\":\"2026-10-23T09:00:00Z\",\"expiresAt\":\"\"},{\"id\":\"E2\",\"subject\":\"U1\",\"purpose\":\"NEWSLETTER\",\"channel\":\"EMAIL\",\"status\":\"REVOKED\",\"at\":\"2026-10-23T10:00:00Z\",\"expiresAt\":\"\"}]}"))
        .andExpect(status().isUnauthorized());
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void httpAllowsAuthenticatedComputation() throws Exception {
    var mvc = MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();
    mvc.perform(
            post("/api/workbench/consent-event-snapshot")
                .principal(new TestingAuthenticationToken("test-user", "unused", "ROLE_OPERATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"asOf\":\"2026-10-23T11:00:00Z\",\"purpose\":\"NEWSLETTER\",\"channel\":\"EMAIL\",\"subjects\":[\"U1\",\"U2\"],\"events\":[{\"id\":\"E1\",\"subject\":\"U1\",\"purpose\":\"NEWSLETTER\",\"channel\":\"EMAIL\",\"status\":\"GRANTED\",\"at\":\"2026-10-23T09:00:00Z\",\"expiresAt\":\"\"},{\"id\":\"E2\",\"subject\":\"U1\",\"purpose\":\"NEWSLETTER\",\"channel\":\"EMAIL\",\"status\":\"REVOKED\",\"at\":\"2026-10-23T10:00:00Z\",\"expiresAt\":\"\"}]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void httpRejectsInvalidInput() throws Exception {
    var mvc = MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();
    mvc.perform(
            post("/api/workbench/consent-event-snapshot")
                .principal(new TestingAuthenticationToken("test-user", "unused", "ROLE_OPERATOR"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false));
  }

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void rejectsAnonymousAuthenticationObject() {
    var actor =
        new AnonymousAuthenticationToken(
            "test-key",
            "anonymous",
            List.of(
                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                    "ROLE_ANONYMOUS")));
    assertEquals(
        401, new AiWorkbenchController(service).run(sample(), actor).getStatusCode().value());
  }
}
