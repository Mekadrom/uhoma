using System.ComponentModel.DataAnnotations;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.DTOs.Upsert;
using Uhoma.CRUDServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace Uhoma.CRUDServer.Controllers;

[ApiController]
[Route("[controller]")]
public class ActionController : ControllerBase
{
    private readonly ILogger<ActionController> _logger;
    private readonly IActionService _actionService;

    public ActionController(ILogger<ActionController> logger,
                            IActionService actionService)
    {
        _logger = logger;
        _actionService = actionService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ActionDto>>> Get([Required] int nodeId, int? id, string? name)
    {
        var result = await _actionService.Search(nodeId, id, name);
        if (result == null || (result is IEnumerable<ActionDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No actions found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<ActionDto>> Upsert([FromBody] ActionUpsertDto actionDto)
    {
        return Ok(await _actionService.Upsert(actionDto.NodeId, actionDto.Id, actionDto.Name, actionDto.ActionHandlerId));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int nodeId, [Required] int id)
    {
        return Ok(await _actionService.Delete(nodeId, id));
    }
}
