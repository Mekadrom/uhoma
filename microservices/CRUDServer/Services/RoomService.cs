using Uhoma.CRUDServer.Data;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Services;

public class RoomService : IRoomService
{
    private readonly ILogger<RoomService> _logger;
    private readonly ApplicationDbContext _context;

    public RoomService(ILogger<RoomService> logger,
                       ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<RoomDto>> Search(int homeSeq, int? roomSeq, string? name)
    {
        return await _context.Rooms
            .Where(r => r.HomeSeq == homeSeq
                && (roomSeq == null || r.RoomSeq == roomSeq)
                && (string.IsNullOrEmpty(name) || r.Name.Contains(name)))
            .Select(r => new RoomDto()
            {
                HomeId = r.HomeSeq,
                Id = r.RoomSeq,
                Name = r.Name,
            }).ToListAsync();
    }

    public async Task<RoomDto?> Upsert(int homeSeq, int? roomSeq, string? name)
    {
        var room = await _context.Rooms
            .FirstOrDefaultAsync(r => r.HomeSeq == homeSeq
                && (roomSeq == null || r.RoomSeq == roomSeq));

        if (room == null)
        {
            room = new Room
            {
                HomeSeq = homeSeq,
            };
            _context.Rooms.Add(room);
        }

        if (name != null)
        {
            room.Name = name;
        }

        await _context.SaveChangesAsync();
        return new RoomDto
        {
            HomeId = room.HomeSeq,
            Id = room.RoomSeq,
            Name = room.Name
        };
    }

    public async Task<bool> Delete(int homeSeq, int roomSeq)
    {
        var room = await _context.Rooms
            .FirstOrDefaultAsync(r => r.HomeSeq == homeSeq
                && r.RoomSeq == roomSeq);

        if (room == null)
        {
            return false;
        }

        _context.Rooms.Remove(room);
        await _context.SaveChangesAsync();
        return true;
    }
}
