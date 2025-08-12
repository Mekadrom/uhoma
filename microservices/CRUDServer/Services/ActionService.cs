using Uhoma.CRUDServer.Data;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Services;

public class ActionService : IActionService
{
    private readonly ILogger<ActionService> _logger;
    private readonly ApplicationDbContext _context;

    public ActionService(ILogger<ActionService> logger,
                         ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<ActionDto>> Search(int nodeSeq, int? actionSeq, string? name)
    {
        return await _context.Actions
            .Where(a => a.NodeSeq == nodeSeq
                && (actionSeq == null || a.ActionSeq == actionSeq)
                && (string.IsNullOrEmpty(name) || a.Name.Contains(name)))
            .Select(a => new ActionDto()
            {
                NodeId = a.NodeSeq,
                Id = a.ActionSeq,
                Name = a.Name,
            }).ToListAsync();
    }

    public async Task<ActionDto?> Upsert(int nodeSeq, int? actionSeq, string? name, int? actionHandlerSeq)
    {
        var action = await _context.Actions
            .FirstOrDefaultAsync(a => a.NodeSeq == nodeSeq
                && (actionSeq == null || a.ActionSeq == actionSeq));

        if (action == null)
        {
            action = new ActionEntity
            {
                NodeSeq = nodeSeq,
            };
            _context.Actions.Add(action);
        }

        if (name != null)
        {
            action.Name = name;
        }
        if (actionHandlerSeq != null)
        {
            action.ActionHandlerSeq = actionHandlerSeq.Value;
        }

        await _context.SaveChangesAsync();
        return new ActionDto
        {
            NodeId = action.NodeSeq,
            Id = action.ActionSeq,
            ActionHandlerId = action.ActionHandlerSeq,
            Name = action.Name
        };
    }

    public async Task<bool> Delete(int nodeSeq, int actionSeq)
    {
        var action = await _context.Actions
            .FirstOrDefaultAsync(a => a.NodeSeq == nodeSeq
                && a.ActionSeq == actionSeq);

        if (action == null)
        {
            return false;
        }

        _context.Actions.Remove(action);
        await _context.SaveChangesAsync();
        return true;
    }
}
