using System.ComponentModel.DataAnnotations;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.DTOs.Upsert;
using HomeAssistantMainServer.Services;
using Microsoft.AspNetCore.Mvc;

namespace HomeAssistantMainServer.Controllers;

[ApiController]
[Route("[controller]")]
public class RoomLinkController : ControllerBase
{
    private readonly ILogger<RoomLinkController> _logger;
    private readonly IRoomLinkService _roomLinkService;

    public RoomLinkController(ILogger<RoomLinkController> logger,
                              IRoomLinkService roomLinkService)
    {
        _logger = logger;
        _roomLinkService = roomLinkService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<RoomLinkDto>>> Get([Required] int startRoomId, [Required] int endRoomId, int? id)
    {
        // todo: Implement user home sequence retrieval from JWT or context
        var result = await _roomLinkService.Search(startRoomId, endRoomId, id);
        if (result == null || (result is IEnumerable<RoomLinkDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No room links found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<RoomLinkDto?>> Upsert([FromBody] RoomLinkUpsertDto roomLinkDto)
    {
        return Ok(await _roomLinkService.Upsert(roomLinkDto.StartRoomId, roomLinkDto.EndRoomId, roomLinkDto.Id, roomLinkDto.TransitionLocationDef));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int id)
    {
        return Ok(await _roomLinkService.Delete(id));
    }
}
