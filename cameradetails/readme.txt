SQL:

Get Cameras groups

SELECT d.name, string_agg(g.groups, ', ' ORDER BY g.groups) gp FROM camera_details d
LEFT JOIN camera_group g
ON g.camera_id = d.id
GROUP BY d.name
ORDER BY gp, d.name;