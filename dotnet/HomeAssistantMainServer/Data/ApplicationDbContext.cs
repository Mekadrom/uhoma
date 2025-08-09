using System.Reflection;
using HomeAssistantMainServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Data;

public class ApplicationDbContext : DbContext
{
    public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
        : base(options) { }

    public DbSet<ActionHandler> ActionHandlers { get; set; }
    public DbSet<ActionParameterType> ActionParameterTypes { get; set; }
    public DbSet<Home> Homes { get; set; }
    public DbSet<Room> Rooms { get; set; }
    public DbSet<Node> Nodes { get; set; }
    public DbSet<ActionEntity> Actions { get; set; }
    public DbSet<ActionParameter> ActionParameters { get; set; }
    public DbSet<RoomLink> RoomLinks { get; set; }
    public DbSet<User> Users { get; set; }

    public override int SaveChanges()
    {
        ProcessTimestampAttributes();
        return base.SaveChanges();
    }

    public override Task<int> SaveChangesAsync(CancellationToken cancellationToken = default)
    {
        ProcessTimestampAttributes();
        return base.SaveChangesAsync(cancellationToken);
    }

    /// <summary>
    /// credit to claude
    /// </summary>
    private void ProcessTimestampAttributes()
    {
        var now = DateTime.UtcNow;
        foreach (var entry in ChangeTracker.Entries())
        {
            if (entry.State == EntityState.Added || entry.State == EntityState.Modified)
            {
                foreach (var property in entry.Properties)
                {
                    if (property.Metadata.PropertyInfo == null)
                    {
                        continue;
                    }
                    if (property.Metadata.PropertyInfo.GetCustomAttribute<CreatedDateAttribute>() != null && entry.State == EntityState.Added)
                    {
                        property.CurrentValue = now;
                    }
                    if (property.Metadata.PropertyInfo.GetCustomAttribute<UpdatedDateAttribute>() != null)
                    {
                        property.CurrentValue = now;
                    }
                }
            }
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Automatically apply converters based on attributes
        foreach (var entityType in modelBuilder.Model.GetEntityTypes())
        {
            foreach (var property in entityType.GetProperties())
            {
                var clrProperty = property.PropertyInfo;
                if (clrProperty != null && 
                    clrProperty.GetCustomAttributes(typeof(CommaSeparatedSetAttribute), false).Any())
                {
                    property.SetValueConverter(new StringSetToCommaDelimitedConverter());
                }
            }
        }
    }
}
