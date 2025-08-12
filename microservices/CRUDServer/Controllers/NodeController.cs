using System.ComponentModel.DataAnnotations;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.DTOs.Upsert;
using Uhoma.CRUDServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace Uhoma.CRUDServer.Controllers;

[ApiController]
[Route("[controller]")]
public class NodeController : ControllerBase
{
    private readonly ILogger<NodeController> _logger;
    private readonly INodeService _nodeService;

    public NodeController(ILogger<NodeController> logger,
                          INodeService nodeService)
    {
        _logger = logger;
        _nodeService = nodeService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<NodeDto>>> Get([Required] int roomId, int? id, string? name)
    {
        var result = await _nodeService.Search(roomId, id, name);
        if (result == null || (result is IEnumerable<NodeDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No nodes found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<NodeDto>> Upsert([FromBody] NodeUpsertDto nodeDto)
    {
        return Ok(await _nodeService.Upsert(nodeDto.RoomId, nodeDto.Id, nodeDto.Name));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int roomId, [Required] int id)
    {
        return Ok(await _nodeService.Delete(roomId, id));
    }
}
