package net.ixitxachitls.dma.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import net.ixitxachitls.dma.entries.NestedEntry;

/**
 * A utility class to parse and generally deal with values.
 */
public class Values
{
  /** Check if a string given for a value is valid. */
  public interface Checker
  {
    public boolean check(String inCheck);
  }

  /** A checker to check for a non empty value. */
  public static final Checker NOT_EMPTY = new Checker()
  {
    @Override
    public boolean check(String inCheck)
    {
      return !inCheck.isEmpty();
    }
  };

  /** Interface for a parser to values. */
  public interface Parser<T>
  {
    public T parse(String ... inValues);
  }

  public Values(Multimap<String, String> inValues)
  {
    m_values = inValues;
  }

  private final Multimap<String, String> m_values;
  private final List<String> m_messages = new ArrayList<>();
  private final Set<String> m_handled = new HashSet<>();

  private boolean m_changed = false;

  public boolean isChanged()
  {
    return m_changed;
  }

  public String use(String inKey, String inDefault)
  {
    return use(inKey, inDefault, Optional.<Checker>absent());
  }

  public Optional<String> use(String inKey, Optional<String> inDefault)
  {
    String value = getFirst(inKey);
    if(value == null)
      return inDefault;

    Optional<String> result = Optional.of(value);
    if(!inDefault.equals(result))
      m_changed = true;

    return result;
  }

  public String use(String inKey, String inDefault, Checker inChecker)
  {
    return use(inKey, inDefault, Optional.of(inChecker));
  }

  public String use(String inKey, String inDefault, Optional<Checker> inChecker)
  {
    String value = getFirst(inKey);
    if(value == null)
      return inDefault;

    if(!inDefault.equals(value))
      m_changed = true;

    if(inChecker.isPresent() && !inChecker.get().check(value))
    {
      m_messages.add("Check for " + inKey + " failed, value not set.");
      return inDefault;
    }

    return value;
  }

  public List<String> use(String inKey, List<String> inDefault)
  {
    return use(inKey, inDefault, null);
  }

  public List<String> use(String inKey, List<String> inDefault,
                          @Nullable Checker inChecker)
  {
    Collection<String> values = get(inKey);
    if(values == null)
      return inDefault;

    List<String> result;
    if(inChecker != null)
    {
      result = new ArrayList<>();
      for(String value : values)
        if(!value.isEmpty())
          if(!inChecker.check(value))
          {
            m_messages.add("Invalid value '" + value + "' for " + inKey);
            return inDefault;
          }
          else
            result.add(value);
    }
    else
      result = new ArrayList<>(values);

    if(inDefault.equals(values))
      return inDefault;

    m_changed = true;
    return result;
  }

  public <T> T use(String inKey, T inDefault, Value.Parser<T> inParser,
                   String ... inParts) {
    List<String []> values = listValues(inKey, inParts);
    if(values.size() == 0 || allEmpty(values.get(0)))
    {
      m_changed = true;
      return inDefault;
    }

    if(values.size() != 1
      || (values.get(0).length != inParts.length
         && inParts != null && inParts.length != 0
         && values.get(0).length != 1))
      throw new IllegalArgumentException("cannot properly parse '" + inKey
                                         + "' for " + Arrays.toString(inParts)
                                         + " with " + values);

    Optional<T> value = inParser.parse(values.get(0));
    if(!value.isPresent())
    {
      m_messages.add("Cannot properly parse " + inKey + " '"
                     + Arrays.toString(values.get(0)) + "'");
      return inDefault;
    }

    if(value.get().equals(inDefault))
      return inDefault;

     m_changed = true;
     return value.get();
  }

  public <T> Optional<T> use(String inKey, Optional<T> inDefault,
                             Value.Parser<T> inParser,
                             String ... inParts) {
    List<String []> values = listValues(inKey, inParts);
    if(values.size() == 0 || allEmpty(values.get(0)))
    {
      m_changed = inDefault.isPresent() ? true : m_changed;
      return Optional.absent();
    }

    if(values.size() != 1
      || (values.get(0).length != inParts.length
         && inParts != null && inParts.length != 0
         && values.get(0).length != 1))
      throw new IllegalArgumentException("cannot properly parse '" + inKey
                                         + "' for " + Arrays.toString(inParts)
                                         + " with " + values);

    Optional<T> value = inParser.parse(values.get(0));
    if(!value.isPresent())
    {
      m_messages.add("Cannot properly parse " + inKey + " '"
                     + Arrays.toString(values.get(0)) + "'");
      return inDefault;
    }

    if(value.equals(inDefault))
      return inDefault;

     m_changed = true;
     return value;
  }

  public <T> List<T> use(String inKey, List<T> inDefault,
                         Value.Parser<T> inParser, String ... inParts)
  {
    List<String []> values = listValues(inKey, inParts);
    List<T> results = new ArrayList<>();
    for(String []single : values)
    {
      if(allEmpty(single))
        continue;

      Optional<T> value = inParser.parse(single);
      if(!value.isPresent())
      {
        m_messages.add("Cannot parse values for " + inKey + ": "
                       + Arrays.toString(single));
        return inDefault;
      }

      results.add(value.get());
    }

    if(inDefault.equals(results))
      return inDefault;

    m_changed = true;
    return results;
  }

  private boolean allEmpty(String ... inValues)
  {
    for(String value : inValues)
      if(value != null && !value.isEmpty()
        && !"unknown".equalsIgnoreCase(value))
        return false;

    return true;
  }

  public List<String> obtainMessages()
  {
    return m_messages;
  }

  private @Nullable Collection<String> get(String inKey)
  {
    m_handled.add(inKey);
    Collection<String> values = m_values.get(inKey);
    if(values == null)
      m_messages.add("Tried to use unknown value " + inKey);

    List<String> cleanedValues = new ArrayList<>(values);
    for (int i = cleanedValues.size() - 1; i >= 0; i--) {
      if (cleanedValues.get(i) == null || cleanedValues.get(i).isEmpty())
        cleanedValues.remove(i);
      else
        break;
    }

    return cleanedValues;
  }

  private @Nullable String getFirst(String inKey)
  {
    Collection<String> values = get(inKey);
    if(values == null || values.isEmpty())
      return null;

    if(values.size() != 1)
      m_messages.add("Found multiple values for " + inKey
                     + ", expected single value.");

    return values.iterator().next();
  }

  private List<String []> listValues(String inKey, String ... inParts)
  {
    List<String []> values = new ArrayList<>();
    if (inParts == null || inParts.length == 0)
      for(String value : get(inKey))
        values.add(new String [] { value });
    else
      // Convert from a -> [], b -> [] ...
      // to [a1, b1, ...], [a2, b2, ...]
      for(int i = 0; i < inParts.length; i++)
      {
        int j = 0;
        for(String value : get(inKey + "." + inParts[i]))
        {
          String []single;
          if (values.size() <= j)
          {
            single = new String[inParts.length];
            values.add(single);
          }
          else
            single = values.get(j);

          single[i] = value;
          j++;
        }
      }

    return values;
  }

  public @Nullable Integer use(String inKey, Integer inDefault)
  {
    String value = getFirst(inKey);
    if(value == null || value.isEmpty())
      return inDefault;

    try
    {
      int intValue = Integer.parseInt(value);
      if(inDefault != null && intValue == inDefault)
        return inDefault;

      m_changed = true;
      return intValue;
    }
    catch(NumberFormatException e)
    {
      m_messages.add("Cannot parse number for " + inKey);
      return null;
    }
  }

  public <E extends NestedEntry>
  List<E> useEntries(String inKey, List<E> inDefault,
                     NestedEntry.Creator<E> inCreator)
  {
    // create sub values list
    String prefix = inKey + ".";
    List<ListMultimap<String, String>> values = new ArrayList<>();
    for (String key : m_values.keySet())
      if (key.startsWith(prefix))
      {
        if (values.isEmpty())
          for (@SuppressWarnings("unused") String value : m_values.get(key))
            values.add(ArrayListMultimap.<String, String>create());

        String subkey = key.substring(prefix.length());
        int i = 0;
        for (String value : m_values.get(key))
          values.get(i++).put(subkey, value);
      }

    List<E> entries = new ArrayList<E>();
    for (ListMultimap<String, String> submap : values)
    {
      E entry = inCreator.create();
      entry.set(new Values(submap));
      entries.add(entry);
    }

    if (inDefault.equals(entries))
      return inDefault;

    m_changed = true;
    return entries;
  }
}
