using Uhoma.CRUDServer.Data;
using Uhoma.CRUDServer.Models.DTOs;
using Uhoma.CRUDServer.Models.Entities;
using Microsoft.EntityFrameworkCore;

namespace Uhoma.CRUDServer.Services;

public class NodeService : INodeService
{
    private readonly ILogger<NodeService> _logger;
    private readonly ApplicationDbContext _context;

    public NodeService(ILogger<NodeService> logger,
                       ApplicationDbContext context)
    {
        _logger = logger;
        _context = context;
    }

    public async Task<IEnumerable<NodeDto>> Search(int roomSeq, int? nodeSeq, string? name)
    {
        return await _context.Nodes
            .Where(n => n.RoomSeq == roomSeq
                && (nodeSeq == null || n.NodeSeq == nodeSeq)
                && (string.IsNullOrEmpty(name) || n.Name.Contains(name)))
            .Select(n => new NodeDto()
            {
                RoomId = n.RoomSeq,
                Id = n.NodeSeq,
                Name = n.Name,
            }).ToListAsync();
    }

    public async Task<NodeDto?> Upsert(int roomSeq, int? nodeSeq, string name)
    {
        var node = await _context.Nodes
            .FirstOrDefaultAsync(n => n.RoomSeq == roomSeq
                && (nodeSeq == null || n.NodeSeq == nodeSeq));

        if (node == null)
        {
            node = new Node
            {
                RoomSeq = roomSeq,
            };
            _context.Nodes.Add(node);
        }

        node.Name = name;

        await _context.SaveChangesAsync();
        return new NodeDto
        {
            RoomId = node.RoomSeq,
            Id = node.NodeSeq,
            Name = node.Name
        };
    }

    public async Task<bool> Delete(int roomSeq, int nodeSeq)
    {
        var node = await _context.Nodes
            .FirstOrDefaultAsync(n => n.RoomSeq == roomSeq
                && n.NodeSeq == nodeSeq);

        if (node == null)
        {
            return false;
        }

        _context.Nodes.Remove(node);
        await _context.SaveChangesAsync();
        return true;
    }
}
