using Uhoma.CRUDServer.Data;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Services;

public class ActionParameterTypeService : IActionParameterTypeService
{
    private readonly ILogger<ActionParameterTypeService> _logger;
    private readonly ApplicationDbContext _context;

    public ActionParameterTypeService(ILogger<ActionParameterTypeService> logger, ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<ActionParameterTypeDto>> Search(int homeSeq, int? actionParameterTypeSeq, string? name)
    {
        return await _context.ActionParameterTypes
            .Where(a => (a.HomeSeq == homeSeq || a.HomeSeq == 1)
                && (actionParameterTypeSeq == null || a.ActionParameterTypeSeq == actionParameterTypeSeq)
                && (string.IsNullOrEmpty(name) || a.Name.Contains(name)))
            .Select(a => new ActionParameterTypeDto()
            {
                HomeId = a.HomeSeq,
                Id = a.ActionParameterTypeSeq,
                Name = a.Name,
                TypeDef = a.TypeDef
            })
            .ToListAsync();
    }

    public async Task<ActionParameterTypeDto?> Upsert(int homeSeq, int? actionParameterTypeSeq, string? name, string? typeDef)
    {
        var actionParameterType = await _context.ActionParameterTypes
            .FirstOrDefaultAsync(a => a.HomeSeq == homeSeq
                && (actionParameterTypeSeq == null || a.ActionParameterTypeSeq == actionParameterTypeSeq));

        if (actionParameterType == null)
        {
            actionParameterType = new ActionParameterType()
            {
                HomeSeq = homeSeq
            };
            _context.ActionParameterTypes.Add(actionParameterType);
        }

        if (name != null)
        {
            actionParameterType.Name = name;
        }
        if (typeDef != null)
        {
            actionParameterType.TypeDef = typeDef;
        }

        await _context.SaveChangesAsync();

        return new ActionParameterTypeDto
        {
            HomeId = actionParameterType.HomeSeq,
            Id = actionParameterType.ActionParameterTypeSeq,
            Name = actionParameterType.Name,
            TypeDef = actionParameterType.TypeDef
        };
    }

    public async Task<bool> Delete(int homeSeq, int actionParameterTypeSeq)
    {
        var actionParameterType = await _context.ActionParameterTypes
            .FirstOrDefaultAsync(a => a.HomeSeq == homeSeq && a.ActionParameterTypeSeq == actionParameterTypeSeq);
        if (actionParameterType == null)
        {
            return false;
        }

        _context.ActionParameterTypes.Remove(actionParameterType);
        await _context.SaveChangesAsync();
        return true;
    }
}
