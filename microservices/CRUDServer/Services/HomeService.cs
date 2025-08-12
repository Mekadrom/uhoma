using Uhoma.CRUDServer.Data;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Services;

public class HomeService : IHomeService
{
    private readonly ILogger<HomeService> _logger;
    private readonly ApplicationDbContext _context;

    public HomeService(ILogger<HomeService> logger,
                       ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<HomeDto>> Search(int userLoginSeq, int? homeSeq, string? name)
    {
        return await _context.Homes
            .Where(h => h.OwnerUserLoginSeq == userLoginSeq
                && (homeSeq == null || h.HomeSeq == homeSeq)
                && (string.IsNullOrEmpty(name) || h.Name.Contains(name)))
            .Select(h => new HomeDto()
            {
                Id = h.HomeSeq,
                Name = h.Name,
                Type = h.Type,
            }).ToListAsync();
    }

    public async Task<HomeDto?> Upsert(int userLoginSeq, int? homeSeq, string? name, string? type)
    {
        var home = await _context.Homes
            .FirstOrDefaultAsync(h => h.OwnerUserLoginSeq == userLoginSeq
                && (homeSeq == null || h.HomeSeq == homeSeq));

        if (home == null)
        {
            home = new Home
            {
                OwnerUserLoginSeq = userLoginSeq
            };
            _context.Homes.Add(home);
        }

        if (name != null)
        {
            home.Name = name;
        }
        if (type != null)
        {
            home.Type = type;
        }

        await _context.SaveChangesAsync();
        return new HomeDto
        {
            Id = home.HomeSeq,
            Name = home.Name,
            Type = home.Type
        };
    }

    public async Task<bool> Delete(int userLoginSeq, int homeSeq)
    {
        var home = await _context.Homes
            .FirstOrDefaultAsync(h => h.OwnerUserLoginSeq == userLoginSeq
                && h.HomeSeq == homeSeq);

        if (home == null)
        {
            return false;
        }

        _context.Homes.Remove(home);
        await _context.SaveChangesAsync();
        return true;
    }
}
