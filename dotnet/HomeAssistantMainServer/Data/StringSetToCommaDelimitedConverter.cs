using Microsoft.EntityFrameworkCore.Storage.ValueConversion;

public class StringSetToCommaDelimitedConverter : ValueConverter<HashSet<string>, string>
{
    public StringSetToCommaDelimitedConverter() : base(v => string.Join(',', v), v => v.Split(',', StringSplitOptions.RemoveEmptyEntries).ToHashSet()) { }
}
