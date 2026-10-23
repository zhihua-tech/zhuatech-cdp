/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.cdp.workbench;

import static cn.zhuatech.cdp.workbench.WorkbenchInput.*;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  public Map<String, Object> execute(Map<String, Object> r) {
    validate(r);

    keys(r, "asOf", "purpose", "channel", "subjects", "events");
    var asOf = instant(text(r, "asOf", 40));
    String purpose = id(r, "purpose"), channel = id(r, "channel");
    var subjects = strings(r, "subjects", 1, 300);
    if (new HashSet<>(subjects).size() != subjects.size())
      throw new IllegalArgumentException("目标客户重复");
    Set<String> ids = new HashSet<>();
    Set<List<String>> moments = new HashSet<>();
    Map<String, Map<String, Object>> latest = new HashMap<>();
    int future = 0, otherScope = 0;
    for (var e : rows(r, "events", 0, 500)) {
      keys(e, "id", "subject", "purpose", "channel", "status", "at", "expiresAt");
      String id = id(e, "id");
      unique(ids, id);
      String subject = id(e, "subject"),
          ep = id(e, "purpose"),
          ec = id(e, "channel"),
          status = text(e, "status", 10);
      if (!Set.of("GRANTED", "REVOKED").contains(status))
        throw new IllegalArgumentException("授权状态错误");
      var at = instant(text(e, "at", 40));
      Object raw = e.get("expiresAt");
      if (!(raw instanceof String expiry)) throw new IllegalArgumentException("到期时间须为UTC时间或空字符串");
      if (!expiry.isEmpty() && (!status.equals("GRANTED") || !instant(expiry).isAfter(at)))
        throw new IllegalArgumentException("授权到期时间或撤回记录错误");
      if (!moments.add(List.of(subject, ep, ec, at.toString())))
        throw new IllegalArgumentException("同主体用途渠道在同一时点存在多条事件，须先复核");
      if (!ep.equals(purpose) || !ec.equals(channel)) {
        otherScope++;
        continue;
      }
      if (at.isAfter(asOf)) {
        future++;
        continue;
      }
      var old = latest.get(subject);
      if (old == null || instant(old.get("at").toString()).isBefore(at)) latest.put(subject, e);
    }
    List<Map<String, Object>> out = new ArrayList<>();
    for (String subject : subjects) {
      var e = latest.get(subject);
      String state = e == null ? "NO_RECORD" : e.get("status").toString();
      if (state.equals("GRANTED")
          && !e.get("expiresAt").toString().isEmpty()
          && !asOf.isBefore(instant(e.get("expiresAt").toString()))) state = "EXPIRED";
      out.add(
          Map.of(
              "subject",
              subject,
              "state",
              state,
              "eligibleInSnapshot",
              state.equals("GRANTED"),
              "evidenceEvent",
              e == null ? "" : e.get("id")));
    }
    return Map.of(
        "purpose",
        purpose,
        "channel",
        channel,
        "subjects",
        out,
        "futureEventsExcluded",
        future,
        "otherScopeExcluded",
        otherScope,
        "automaticActivation",
        false);
  }
}
