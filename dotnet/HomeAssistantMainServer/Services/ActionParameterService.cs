using HomeAssistantMainServer.Data;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace HomeAssistantMainServer.Services;

public class ActionParameterService : IActionParameterService
{
    private readonly ILogger<ActionParameterService> _logger;
    private readonly ApplicationDbContext _context;

    public ActionParameterService(ILogger<ActionParameterService> logger, ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<ActionParameterDto>> Search(int actionSeq, int? actionParameterSeq, string? name)
    {
        return await _context.ActionParameters
            .Where(a => a.ActionSeq == actionSeq
                && (actionParameterSeq == null || a.ActionParameterSeq == actionParameterSeq)
                && (string.IsNullOrEmpty(name) || a.Name.Contains(name)))
            .Select(a => new ActionParameterDto()
            {
                ActionId = a.ActionSeq,
                ActionParameterTypeId = a.ActionParameterTypeSeq,
                Id = a.ActionParameterSeq,
                Name = a.Name,
                DefaultValue = a.DefaultValue
            })
            .ToListAsync();
    }

    public async Task<ActionParameterDto?> Upsert(int actionSeq, int? actionParameterSeq, string? name, string? defaultValue, int? actionParameterTypeSeq)
    {
        var actionParameter = await _context.ActionParameters
            .FirstOrDefaultAsync(a => a.ActionSeq == actionSeq
                && (actionParameterSeq == null || a.ActionParameterSeq == actionParameterSeq));

        if (actionParameter == null)
        {
            actionParameter = new ActionParameter()
            {
                ActionSeq = actionSeq
            };
            _context.ActionParameters.Add(actionParameter);
        }

        if (name != null)
        {
            actionParameter.Name = name;
        }
        if (defaultValue != null)
        {
            actionParameter.DefaultValue = defaultValue;
        }
        if (actionParameterTypeSeq != null)
        {
            actionParameter.ActionParameterTypeSeq = actionParameterTypeSeq.Value;
        }

        await _context.SaveChangesAsync();

        return new ActionParameterDto
        {
            ActionId = actionParameter.ActionSeq,
            ActionParameterTypeId = actionParameter.ActionParameterTypeSeq,
            Id = actionParameter.ActionParameterSeq,
            Name = actionParameter.Name,
            DefaultValue = actionParameter.DefaultValue
        };
    }

    public async Task<bool> Delete(int actionSeq, int actionParameterSeq)
    {
        var actionParameter = await _context.ActionParameters
            .FirstOrDefaultAsync(a => a.ActionSeq == actionSeq && a.ActionParameterSeq == actionParameterSeq);
        if (actionParameter == null)
        {
            return false;
        }

        _context.ActionParameters.Remove(actionParameter);
        await _context.SaveChangesAsync();
        return true;
    }
}
