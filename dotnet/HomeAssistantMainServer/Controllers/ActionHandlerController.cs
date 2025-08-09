using System.ComponentModel.DataAnnotations;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.DTOs.Upsert;
using HomeAssistantMainServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace HomeAssistantMainServer.Controllers;

[ApiController]
[Route("[controller]")]
public class ActionHandlerController : ControllerBase
{
    private readonly ILogger<ActionHandlerController> _logger;
    private readonly IActionHandlerService _actionHandlerService;

    public ActionHandlerController(ILogger<ActionHandlerController> logger,
                                   IActionHandlerService actionHandlerService)
    {
        _logger = logger;
        _actionHandlerService = actionHandlerService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ActionHandlerDto>>> Get([Required] int homeId, int? id, string? name)
    {
        // todo: Implement user home sequence retrieval from JWT or context
        var result = await _actionHandlerService.Search(homeId, id, name);
        if (result == null || (result is IEnumerable<ActionHandlerDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No action handlers found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<ActionHandlerDto>> Upsert([FromBody] ActionHandlerUpsertDto actionHandlerDto)
    {
        return Ok(await _actionHandlerService.Upsert(actionHandlerDto.HomeId, actionHandlerDto.Id, actionHandlerDto.Name, actionHandlerDto.HandlerDef));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int homeId, [Required] int id)
    {
        return Ok(await _actionHandlerService.Delete(homeId, id));
    }
}
