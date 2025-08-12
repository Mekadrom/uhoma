using System.ComponentModel.DataAnnotations;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.DTOs.Upsert;
using Uhoma.CRUDServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace Uhoma.CRUDServer.Controllers;

[ApiController]
[Route("[controller]")]
public class ActionParameterController : ControllerBase
{
    private readonly ILogger<ActionParameterController> _logger;
    private readonly IActionParameterService _actionParameterService;

    public ActionParameterController(ILogger<ActionParameterController> logger,
                                     IActionParameterService actionParameterService)
    {
        _logger = logger;
        _actionParameterService = actionParameterService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ActionParameterDto>>> Get([Required] int actionId, int? id, string? name)
    {
        // todo: Implement user home sequence retrieval from JWT or context
        var result = await _actionParameterService.Search(actionId, id, name);
        if (result == null || (result is IEnumerable<ActionParameterDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No action parameters found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<ActionParameterDto?>> Upsert([FromBody] ActionParameterUpsertDto actionParameterDto)
    {
        return Ok(await _actionParameterService.Upsert(actionParameterDto.ActionId, actionParameterDto.Id, actionParameterDto.Name, actionParameterDto.DefaultValue, actionParameterDto.ActionParameterTypeId));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int actionId, [Required] int id)
    {
        return Ok(await _actionParameterService.Delete(actionId, id));
    }
}
