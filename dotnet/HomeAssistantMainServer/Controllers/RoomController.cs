using System.ComponentModel.DataAnnotations;
using HomeAssistantMainServer.Models.DTOs;
using HomeAssistantMainServer.Models.DTOs.Upsert;
using HomeAssistantMainServer.Services;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("[controller]")]
public class RoomController : ControllerBase
{
    private readonly ILogger<RoomController> _logger;
    private readonly IRoomService _roomService;

    public RoomController(ILogger<RoomController> logger,
                          IRoomService roomService)
    {
        _logger = logger;
        _roomService = roomService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<RoomDto>>> Get([Required] int homeId, int? id, string? name)
    {
        var result = await _roomService.Search(homeId, id, name);
        if (result == null || (result is IEnumerable<RoomDto> enumerable && !enumerable.Any()))
        {
            return NotFound("No rooms found matching the search criteria.");
        }
        return Ok(result);
    }

    [HttpPut]
    [HttpPost]
    public async Task<ActionResult<RoomDto>> Upsert([FromBody] RoomUpsertDto roomDto)
    {
        return Ok(await _roomService.Upsert(roomDto.HomeId, roomDto.Id, roomDto.Name));
    }

    [HttpDelete]
    public async Task<ActionResult<bool>> Delete([Required] int homeId, [Required] int id)
    {
        return Ok(await _roomService.Delete(homeId, id));
    }
}
