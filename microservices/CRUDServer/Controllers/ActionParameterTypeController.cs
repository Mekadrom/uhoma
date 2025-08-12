using System.ComponentModel.DataAnnotations;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.DTOs.Upsert;
using Uhoma.CRUDServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace Uhoma.CRUDServer.Controllers;

[ApiController]
[Route("[controller]")]
public class ActionParameterTypeController : ControllerBase
{
    private readonly ILogger<ActionParameterTypeController> _logger;
    private readonly IActionParameterTypeService _actionParameterTypeService;

    public ActionParameterTypeController(ILogger<ActionParameterTypeController> logger,
                                         IActionParameterTypeService actionParameterTypeService)
    {
        _logger = logger;
        _actionParameterTypeService = actionParameterTypeService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ActionParameterTypeDto>>> Get([Required] int homeId, int? id, string? name)
    {
        // todo: Implement user home sequence retrieval from JWT or context
        var result = await _actionParameterTypeService.Search(homeId, id, name);
        if (result == null || (result is IEnumerable<ActionParameterTypeDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No action parameter types found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<ActionParameterTypeDto?>> Upsert([FromBody] ActionParameterTypeUpsertDto actionParameterTypeDto)
    {
        return Ok(await _actionParameterTypeService.Upsert(actionParameterTypeDto.HomeId, actionParameterTypeDto.Id, actionParameterTypeDto.Name, actionParameterTypeDto.TypeDef));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int homeId, [Required] int id)
    {
        return Ok(await _actionParameterTypeService.Delete(homeId, id));
    }
}
