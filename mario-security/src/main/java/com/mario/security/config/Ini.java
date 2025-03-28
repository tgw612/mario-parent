package com.mario.security.config;

import com.mario.common.exception.SystemException;
import com.mario.common.util.CollectionUtil;
import com.mario.common.util.StringUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.naming.ConfigurationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Ini implements Map<String, Ini.Section> {

  public static final String DEFAULT_SECTION_NAME = ""; //empty string means the first unnamed section
  public static final String DEFAULT_CHARSET_NAME = "UTF-8";

  public static final String COMMENT_POUND = "#";
  public static final String COMMENT_SEMICOLON = ";";
  public static final String SECTION_PREFIX = "[";
  public static final String SECTION_SUFFIX = "]";

  protected static final char ESCAPE_TOKEN = '\\';

  private final Map<String, Section> sections;

  public Ini() {
    this.sections = new LinkedHashMap<String, Section>();
  }

  public Ini(Ini defaults) {
    this();
    if (defaults == null) {
      throw new NullPointerException("Defaults cannot be null.");
    }
    for (Section section : defaults.getSections()) {
      Section copy = new Section(section);
      this.sections.put(section.getName(), copy);
    }
  }

  @Override
  public boolean isEmpty() {
    Collection<Section> sections = this.sections.values();
    if (!sections.isEmpty()) {
      for (Section section : sections) {
        if (!section.isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  public Set<String> getSectionNames() {
    return Collections.unmodifiableSet(sections.keySet());
  }

  public Collection<Section> getSections() {
    return Collections.unmodifiableCollection(sections.values());
  }

  public Section getSection(String sectionName) {
    String name = cleanName(sectionName);
    return sections.get(name);
  }

  public Section addSection(String sectionName) {
    String name = cleanName(sectionName);
    Section section = getSection(name);
    if (section == null) {
      section = new Section(name);
      this.sections.put(name, section);
    }
    return section;
  }

  public Section removeSection(String sectionName) {
    String name = cleanName(sectionName);
    return this.sections.remove(name);
  }

  private static String cleanName(String sectionName) {
    String name = StringUtil.clean(sectionName);
    if (name == null) {
      log.trace(
          "Specified name was null or empty.  Defaulting to the default section (name = \"\")");
      name = DEFAULT_SECTION_NAME;
    }
    return name;
  }

  public void setSectionProperty(String sectionName, String propertyName, String propertyValue) {
    String name = cleanName(sectionName);
    Section section = getSection(name);
    if (section == null) {
      section = addSection(name);
    }
    section.put(propertyName, propertyValue);
  }

  public String getSectionProperty(String sectionName, String propertyName) {
    Section section = getSection(sectionName);
    return section != null ? section.get(propertyName) : null;
  }

  public String getSectionProperty(String sectionName, String propertyName, String defaultValue) {
    String value = getSectionProperty(sectionName, propertyName);
    return value != null ? value : defaultValue;
  }

  public void load(String iniConfig) {
    load(new Scanner(iniConfig));
  }

  public void load(InputStream is) throws ConfigurationException {
    if (is == null) {
      throw new NullPointerException("InputStream argument cannot be null.");
    }
    InputStreamReader isr;
    try {
      isr = new InputStreamReader(is, DEFAULT_CHARSET_NAME);
    } catch (UnsupportedEncodingException e) {
      throw new SystemException(e);
    }
    load(isr);
  }

  public void load(Reader reader) {
    Scanner scanner = new Scanner(reader);
    try {
      load(scanner);
    } finally {
      try {
        scanner.close();
      } catch (Exception e) {
        log.debug("Unable to cleanly close the InputStream scanner.  Non-critical - ignoring.", e);
      }
    }
  }

  private void addSection(String name, StringBuilder content) {
    if (content.length() > 0) {
      String contentString = content.toString();
      String cleaned = StringUtil.clean(contentString);
      if (cleaned != null) {
        Section section = new Section(name, contentString);
        if (!section.isEmpty()) {
          sections.put(name, section);
        }
      }
    }
  }

  public void load(Scanner scanner) {

    String sectionName = DEFAULT_SECTION_NAME;
    StringBuilder sectionContent = new StringBuilder();

    while (scanner.hasNextLine()) {

      String rawLine = scanner.nextLine();
      String line = StringUtil.clean(rawLine);

      if (line == null || line.startsWith(COMMENT_POUND) || line.startsWith(COMMENT_SEMICOLON)) {
        //skip empty lines and comments:
        continue;
      }

      String newSectionName = getSectionName(line);
      if (newSectionName != null) {
        //found a new section - convert the currently buffered one into a Section object
        addSection(sectionName, sectionContent);

        //reset the buffer for the new section:
        sectionContent = new StringBuilder();

        sectionName = newSectionName;

        if (log.isDebugEnabled()) {
          log.debug("Parsing " + SECTION_PREFIX + sectionName + SECTION_SUFFIX);
        }
      } else {
        //normal line - add it to the existing content buffer:
        sectionContent.append(rawLine).append("\n");
      }
    }

    //finish any remaining buffered content:
    addSection(sectionName, sectionContent);
  }

  protected static boolean isSectionHeader(String line) {
    String s = StringUtil.clean(line);
    return s != null && s.startsWith(SECTION_PREFIX) && s.endsWith(SECTION_SUFFIX);
  }

  protected static String getSectionName(String line) {
    String s = StringUtil.clean(line);
    if (isSectionHeader(s)) {
      return cleanName(s.substring(1, s.length() - 1));
    }
    return null;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Ini) {
      Ini ini = (Ini) obj;
      return this.sections.equals(ini.sections);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return this.sections.hashCode();
  }

  @Override
  public String toString() {
    if (CollectionUtil.isEmpty(this.sections)) {
      return "<empty INI>";
    } else {
      StringBuilder sb = new StringBuilder("sections=");
      int i = 0;
      for (Ini.Section section : this.sections.values()) {
        if (i > 0) {
          sb.append(",");
        }
        sb.append(section.toString());
        i++;
      }
      return sb.toString();
    }
  }

  @Override
  public int size() {
    return this.sections.size();
  }

  @Override
  public boolean containsKey(Object key) {
    return this.sections.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return this.sections.containsValue(value);
  }

  @Override
  public Section get(Object key) {
    return this.sections.get(key);
  }

  @Override
  public Section put(String key, Section value) {
    return this.sections.put(key, value);
  }

  @Override
  public Section remove(Object key) {
    return this.sections.remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Section> m) {
    this.sections.putAll(m);
  }

  @Override
  public void clear() {
    this.sections.clear();
  }

  @Override
  public Set<String> keySet() {
    return Collections.unmodifiableSet(this.sections.keySet());
  }

  @Override
  public Collection<Section> values() {
    return Collections.unmodifiableCollection(this.sections.values());
  }

  @Override
  public Set<Entry<String, Section>> entrySet() {
    return Collections.unmodifiableSet(this.sections.entrySet());
  }

  public static class Section implements Map<String, String> {

    private final String name;
    private final Map<String, String> props;

    private Section(String name) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      this.name = name;
      this.props = new LinkedHashMap<String, String>();
    }

    private Section(String name, String sectionContent) {
      if (name == null) {
        throw new NullPointerException("name");
      }
      this.name = name;
      Map<String, String> props;
      if (StringUtil.hasText(sectionContent)) {
        props = toMapProps(sectionContent);
      } else {
        props = new LinkedHashMap<String, String>();
      }
      if (props != null) {
        this.props = props;
      } else {
        this.props = new LinkedHashMap<String, String>();
      }
    }

    private Section(Section defaults) {
      this(defaults.getName());
      putAll(defaults.props);
    }

    //Protected to access in a test case - NOT considered part of Shiro's public API

    protected static boolean isContinued(String line) {
      if (!StringUtil.hasText(line)) {
        return false;
      }
      int length = line.length();
      int backslashCount = 0;
      for (int i = length - 1; i > 0; i--) {
        if (line.charAt(i) == ESCAPE_TOKEN) {
          backslashCount++;
        } else {
          break;
        }
      }
      return backslashCount % 2 != 0;
    }

    private static boolean isKeyValueSeparatorChar(char c) {
      return Character.isWhitespace(c) || c == ':' || c == '=';
    }

    private static boolean isCharEscaped(CharSequence s, int index) {
      return index > 0 && s.charAt(index - 1) == ESCAPE_TOKEN;
    }

    //Protected to access in a test case - NOT considered part of Shiro's public API
    protected static String[] splitKeyValue(String keyValueLine) {
      String line = StringUtil.clean(keyValueLine);
      if (line == null) {
        return null;
      }
      StringBuilder keyBuffer = new StringBuilder();
      StringBuilder valueBuffer = new StringBuilder();

      boolean buildingKey = true; //we'll build the value next:

      for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);

        if (buildingKey) {
          if (isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
            buildingKey = false;//now start building the value
          } else {
            keyBuffer.append(c);
          }
        } else {
          if (valueBuffer.length() == 0 && isKeyValueSeparatorChar(c) && !isCharEscaped(line, i)) {
            //swallow the separator chars before we start building the value
          } else {
            valueBuffer.append(c);
          }
        }
      }

      String key = StringUtil.clean(keyBuffer.toString());
      String value = StringUtil.clean(valueBuffer.toString());

      if (key == null || value == null) {
        String msg = "Line argument must contain a key and a value.  Only one string token was found.";
        throw new IllegalArgumentException(msg);
      }

      log.trace("Discovered key/value pair: {}={}", key, value);

      return new String[]{key, value};
    }

    private static Map<String, String> toMapProps(String content) {
      Map<String, String> props = new LinkedHashMap<String, String>();
      String line;
      StringBuilder lineBuffer = new StringBuilder();
      Scanner scanner = new Scanner(content);
      while (scanner.hasNextLine()) {
        line = StringUtil.clean(scanner.nextLine());
        if (isContinued(line)) {
          //strip off the last continuation backslash:
          line = line.substring(0, line.length() - 1);
          lineBuffer.append(line);
          continue;
        } else {
          lineBuffer.append(line);
        }
        line = lineBuffer.toString();
        lineBuffer = new StringBuilder();
        String[] kvPair = splitKeyValue(line);
        props.put(kvPair[0], kvPair[1]);
      }

      return props;
    }

    public String getName() {
      return this.name;
    }

    @Override
    public void clear() {
      this.props.clear();
    }

    @Override
    public boolean containsKey(Object key) {
      return this.props.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
      return this.props.containsValue(value);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
      return this.props.entrySet();
    }

    @Override
    public String get(Object key) {
      return this.props.get(key);
    }

    @Override
    public boolean isEmpty() {
      return this.props.isEmpty();
    }

    @Override
    public Set<String> keySet() {
      return this.props.keySet();
    }

    @Override
    public String put(String key, String value) {
      return this.props.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
      this.props.putAll(m);
    }

    @Override
    public String remove(Object key) {
      return this.props.remove(key);
    }

    @Override
    public int size() {
      return this.props.size();
    }

    @Override
    public Collection<String> values() {
      return this.props.values();
    }

    @Override
    public String toString() {
      String name = getName();
      if (DEFAULT_SECTION_NAME.equals(name)) {
        return "<default>";
      }
      return name;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Section) {
        Section other = (Section) obj;
        return getName().equals(other.getName()) && this.props.equals(other.props);
      }
      return false;
    }

    @Override
    public int hashCode() {
      return this.name.hashCode() * 31 + this.props.hashCode();
    }
  }

}
