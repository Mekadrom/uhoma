using HomeAssistantMainServer.Data;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Services;

public class ActionHandlerService : IActionHandlerService
{
    private readonly ILogger<ActionHandlerService> _logger;
    private readonly ApplicationDbContext _context;

    public ActionHandlerService(ILogger<ActionHandlerService> logger, ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<ActionHandlerDto>> Search(int homeSeq, int? actionHandlerSeq, string? name)
    {
        return await _context.ActionHandlers
            .Where(a => (a.HomeSeq == homeSeq || a.HomeSeq == 1)
                && (actionHandlerSeq == null || a.ActionHandlerSeq == actionHandlerSeq)
                && (string.IsNullOrEmpty(name) || a.Name.Contains(name)))
            .Select(a => new ActionHandlerDto()
            {
                HomeId = a.HomeSeq,
                Id = a.ActionHandlerSeq,
                Name = a.Name,
                HandlerDef = a.HandlerDef
            })
            .ToListAsync();
    }

    public async Task<ActionHandlerDto?> Upsert(int homeSeq, int? actionHandlerSeq, string? name, string? handlerDef)
    {
        var actionHandler = await _context.ActionHandlers
            .FirstOrDefaultAsync(a => a.HomeSeq == homeSeq
                && (actionHandlerSeq == null || a.ActionHandlerSeq == actionHandlerSeq));

        if (actionHandler == null)
        {
            actionHandler = new ActionHandler()
            {
                HomeSeq = homeSeq
            };
            _context.ActionHandlers.Add(actionHandler);
        }

        if (name != null)
        {
            actionHandler.Name = name;
        }
        if (handlerDef != null)
        {
            actionHandler.HandlerDef = handlerDef;
        }

        await _context.SaveChangesAsync();

        return new ActionHandlerDto
        {
            HomeId = actionHandler.HomeSeq,
            Id = actionHandler.ActionHandlerSeq,
            Name = actionHandler.Name,
            HandlerDef = actionHandler.HandlerDef
        };
    }

    public async Task<bool> Delete(int homeSeq, int actionHandlerSeq)
    {
        var actionHandler = await _context.ActionHandlers
            .FirstOrDefaultAsync(a => a.HomeSeq == homeSeq && a.ActionHandlerSeq == actionHandlerSeq);
        if (actionHandler == null)
        {
            return false;
        }

        _context.ActionHandlers.Remove(actionHandler);
        await _context.SaveChangesAsync();
        return true;
    }
}
