using HomeAssistantMainServer.Data;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Services;

public class RoomLinkService : IRoomLinkService
{
    private readonly ILogger<RoomLinkService> _logger;
    private readonly ApplicationDbContext _context;

    public RoomLinkService(ILogger<RoomLinkService> logger,
                           ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<RoomLinkDto>> Search(int startRoomSeq, int endRoomSeq, int? roomLinkSeq)
    {
        return await _context.RoomLinks
            .Where(r => r.StartRoomSeq == startRoomSeq
                && r.EndRoomSeq == endRoomSeq
                && (roomLinkSeq == null || r.RoomLinkSeq == roomLinkSeq))
            .Select(r => new RoomLinkDto()
            {
                StartRoomId = r.StartRoomSeq,
                EndRoomId = r.EndRoomSeq,
                Id = r.RoomLinkSeq,
                TransitionLocationDef = r.TransitionLocationDef,
            }).ToListAsync();
    }

    public async Task<RoomLinkDto?> Upsert(int startRoomSeq, int endRoomSeq, int? roomLinkSeq, string transitionLocationDef)
    {
        var entity = await _context.RoomLinks
            .FirstOrDefaultAsync(r => r.StartRoomSeq == startRoomSeq
                && r.EndRoomSeq == endRoomSeq
                && (roomLinkSeq == null || r.RoomLinkSeq == roomLinkSeq));

        if (entity == null)
        {
            entity = new RoomLink
            {
                StartRoomSeq = startRoomSeq,
                EndRoomSeq = endRoomSeq,
            };
            _context.RoomLinks.Add(entity);
        }

        entity.TransitionLocationDef = transitionLocationDef;

        await _context.SaveChangesAsync();
        return new RoomLinkDto
        {
            StartRoomId = entity.StartRoomSeq,
            EndRoomId = entity.EndRoomSeq,
            Id = entity.RoomLinkSeq,
            TransitionLocationDef = entity.TransitionLocationDef
        };
    }

    public async Task<bool> Delete(int roomLinkSeq)
    {
        var entity = await _context.RoomLinks
            .FirstOrDefaultAsync(r => r.RoomLinkSeq == roomLinkSeq);

        if (entity == null)
        {
            return false;
        }

        _context.RoomLinks.Remove(entity);
        await _context.SaveChangesAsync();
        return true;
    }
}
